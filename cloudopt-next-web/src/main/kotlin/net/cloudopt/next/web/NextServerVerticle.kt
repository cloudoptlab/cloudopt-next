/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cloudopt.next.web

import com.alibaba.fastjson.JSONObject
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.*
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.utils.Beaner
import net.cloudopt.next.validator.ValidatorTool
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.web.event.AfterEvent
import net.cloudopt.next.web.event.EventManager
import net.cloudopt.next.web.handler.ErrorHandler
import net.cloudopt.next.web.route.Parameter
import net.cloudopt.next.web.route.RequestBody
import net.cloudopt.next.web.route.SocketJS
import net.cloudopt.next.web.route.WebSocket


/*
 * @author: Cloudopt
 * @Time: 2018/1/17
 * @Description: Cloudopt Next Server Verticle
 */
class NextServerVerticle : AbstractVerticle() {

    val logger = Logger.getLogger(NextServerVerticle::class.java)

    override fun start() {

        val server = NextServer.vertx.createHttpServer(ConfigManager.config.vertxHttpServer)

        val router = Router.router(NextServer.vertx)


        /**
         * Register sockJS
         */
        if (NextServer.sockJSes.size > 0) {
            val sockJSHandler = SockJSHandler.create(NextServer.vertx, ConfigManager.config.socket)
            NextServer.sockJSes.forEach { clazz ->
                val socketAnnotation: SocketJS = clazz.getDeclaredAnnotation(SocketJS::class.java)
                sockJSHandler.socketHandler { sockJSHandler ->
                    val handler = Beaner.newInstance<SockJSResource>(clazz)
                    handler.handler(sockJSHandler)
                }
                if (!socketAnnotation.value.endsWith("/*")) {
                    logger.error("[SOCKET] Url must be end with /* !")
                }
                logger.info("[SOCKET] Registered socket resource: ${socketAnnotation.value} -> ${clazz.name}")
                router.route(socketAnnotation.value).handler(sockJSHandler)
            }
        }

        /**
         * Register websocket
         */
        if (NextServer.webSockets.size > 0) {
            NextServer.webSockets.forEach { clazz ->
                val websocketAnnotation: WebSocket = clazz.getDeclaredAnnotation(WebSocket::class.java)
                router.route(websocketAnnotation.value).handler { context ->
                    try {
                        val userWebSocketConnection = context.request().upgrade()
                        val controllerObj = Beaner.newInstance<WebSocketResource>(clazz)
                        controllerObj.handler(userWebSocketConnection)
                    } catch (e: InstantiationException) {
                        e.printStackTrace()
                        context.response().end()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                        context.response().end()
                    }
                }
                logger.info("[WEBSOCKET] Registered socket resource: ${websocketAnnotation.value} -> ${clazz.name}")

            }
        }

        /**
         * The ResponseContentTypeHandler can set the Content-Type header automatically
         */
        router.route("/*").handler(ResponseContentTypeHandler.create())

        router.route("/*").handler(BodyHandler.create().setBodyLimit(ConfigManager.config.bodyLimit))

        /**
         * Set timeout
         */
        router.route("/*").handler(TimeoutHandler.create(ConfigManager.config.timeout))

        /**
         * Set csrf
         */
        if (ConfigManager.config.waf.csrf) {
            router.route("/*").handler(CSRFHandler.create(ConfigManager.config.waf.encryption))
        }

        /**
         * Register failure handler
         */
        NextServer.logger.info("[FAILURE HANDLER] Registered failure handler：${NextServer.errorHandler::class.java.name}")

        router.route("/*").failureHandler { context ->
            errorProcessing(context)
        }

        for (i in 400..500) {
            router.errorHandler(i) { context ->
                errorProcessing(context)
            }
        }

        /**
         * Register handlers
         */
        NextServer.handlers.forEach { handler ->
            NextServer.logger.info("[HANDLER] Registered handler：${handler::class.java.name}")
            router.route("/*").handler { context ->
                try {
                    if (handler.preHandle(Resource().init(context))) {
                        context.next()
                    } else if (!context.response().ended()) {
                        context.response().end()
                    }
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                    context.response().end()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                    context.response().end()
                }
            }
        }

        router.route("/" + ConfigManager.config.staticPackage + "/*").handler(
                StaticHandler.create().setIndexPage(ConfigManager.config.indexPage)
                        .setIncludeHidden(false).setWebRoot(ConfigManager.config.staticPackage)
        )

        /**
         * Register interceptors
         */
        NextServer.interceptors.forEach { (url, clazz) ->
            router.route(url).handler { context ->
                val resource = Resource()
                resource.init(context)
                val interceptors = clazz.map { Beaner.newInstance<Interceptor>(it.java) }

                val interceptor = interceptors.firstOrNull {
                    !it.intercept(resource)
                }

                if (interceptor != null) {
                    if (!interceptor.response(resource).response.ended()) {
                        resource.end()
                    }

                } else {
                    context.next()
                }
            }
        }

        /**
         * Register validators
         */
        NextServer.validators.forEach { (url, map) ->
            map.keys.forEach { key ->
                val validatorList = map[key]
                validatorList?.forEach { validator ->
                    router.route(key, url).handler { context ->
                        try {
                            val v = Beaner.newInstance<Validator>(validator.java)
                            val resource = Resource()
                            resource.init(context)
                            if (v.validate(resource)) {
                                context.next()
                            } else {
                                v.error(resource)
                            }
                        } catch (e: InstantiationException) {
                            e.printStackTrace()
                            context.response().end()
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                            context.response().end()
                        }
                    }
                }

            }
        }

        if (NextServer.resourceTables.size < 1) {
            router.route("/").blockingHandler { context ->
                context.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")
                context.response().endHandler {
                    NextServer.handlers.forEach { handler ->
                        handler.afterCompletion(Resource().init(context))
                    }
                }
                context.response().end(Welcomer.home())
            }
        }

        /**
         * Register method
         */
        NextServer.resourceTables.forEach { resourceTable ->
            if (resourceTable.blocking) {
                router.route(resourceTable.httpMethod, resourceTable.url).blockingHandler { context ->
                    requestProcessing(resourceTable, context)
                }
            } else {
                router.route(resourceTable.httpMethod, resourceTable.url).handler { context ->
                    requestProcessing(resourceTable, context)
                }
            }

            NextServer.logger.info(
                    "[RESOURCE] Registered resource :${resourceTable.methodName} | ${resourceTable.url}"
            )
        }

        server.requestHandler(router).listen(ConfigManager.config.port) { result ->
            if (result.succeeded()) {
                NextServer.logger.info(
                        "=========================================================================================================="
                )
                NextServer.logger.info("\uD83D\uDC0B Cloudopt Next started success!")
                NextServer.logger.info("http://127.0.0.1:${ConfigManager.config.port}")
                NextServer.logger.info(
                        "=========================================================================================================="
                )

            } else {
                NextServer.logger.error(
                        "=========================================================================================================="
                )
                NextServer.logger.error("\uD83D\uDC0B Cloudopt Next started error! ${result.cause()}")
                NextServer.logger.error(
                        "=========================================================================================================="
                )
            }
        }
    }

    override fun stop() {
    }

    /**
     * This is used to handle http requests where an error occurs, and will automatically call errorHandler when an
     * error occurs. and ends the Http request to avoid long periods of no response. It also automatically outputs
     * error messages via the log class.
     * @param context RoutingContext
     * @see ErrorHandler
     * @see RoutingContext
     */
    private fun errorProcessing(context: RoutingContext) {
        context.response().endHandler {
            NextServer.handlers.forEach { handler ->
                handler.afterCompletion(Resource().init(context))
            }
        }
        val errorHandler = Beaner.newInstance<ErrorHandler>(NextServer.errorHandler)
        errorHandler.init(context)
        errorHandler.handle()
        if (context.failure() != null) {
            logger.error(context.failure().toString())
        }
        if (!errorHandler.response.ended()) {
            errorHandler.end()
        }
    }

    /**
     * is used to process normal http requests, automatically generating new objects from the resource class of the
     * route and calls its invoke method. It also injects parameters depending on whether the method corresponding
     * to the route contains a parameter injection annotation or not. If there is an @afterEvent annotation on the
     * method, it will automatically execute the afterEvent.
     * @param resourceTable ResourceTable
     * @param context RoutingContext
     * @see ResourceTable
     * @see RoutingContext
     * @see AfterEvent
     */
    private fun requestProcessing(resourceTable: ResourceTable, context: RoutingContext) {
        try {
            val controllerObj = Beaner.newInstance<Resource>(resourceTable.clazz)
            controllerObj.init(context)
            /**
             * Check if the method has a list of parameters
             */
            val m = if (resourceTable.parameterTypes.isNotEmpty()) {
                resourceTable.clazz.getDeclaredMethod(resourceTable.methodName, *resourceTable.parameterTypes)
            } else {
                resourceTable.clazz.getDeclaredMethod(resourceTable.methodName)
            }

            if (NextServer.handlers.isNotEmpty() || m.getAnnotation(AfterEvent::class.java) != null) {
                context.response().endHandler {
                    /**
                     * Executes a global handler that is called at the end of the route
                     */
                    NextServer.handlers.forEach { handler ->
                        handler.afterCompletion(Resource().init(context))
                    }

                    /**
                     * If the afterEvent annotation is included, the event is automatically sent to EventBus after the
                     * http request ends
                     * @see AfterEvent
                     */
                    if (m.getAnnotation(AfterEvent::class.java) != null) {
                        val afterEvent = m.getAnnotation(AfterEvent::class.java)
                        for (topic in afterEvent.value) {
                            EventManager.sendObject(topic, context.data(), "map")
                        }
                    }
                }
            }

            /**
             * If the method supports parameter injection, it will automatically extract the corresponding parameter
             * and inject it
             */
            if (m.parameters.isNotEmpty()) {
                val arr = arrayListOf<Any>()
                for (para in m.parameters) {
                    val parameterAnnotation = para.getAnnotation(Parameter::class.java)
                    if (parameterAnnotation != null) {
                        getParaByType(para.getAnnotation(Parameter::class.java).value, para, controllerObj)?.let {
                            arr.add(
                                    it
                            )
                        }
                    }
                    val requestBodyAnnotation = para.getAnnotation(RequestBody::class.java)
                    if (requestBodyAnnotation != null) {
                        controllerObj.getBodyJson(para.type)?.let { arr.add(it) }
                    }
                }
                /**
                 * Support for verifying injected parameters
                 * @see ValidatorTool
                 */
                val validatorResult = ValidatorTool.validateParameters(controllerObj, m, arr.toArray())
                if (validatorResult.result) {
                    m.invoke(controllerObj, *arr.toArray())
                } else {
                    controllerObj.context.put("errorMessage", validatorResult.message)
                    controllerObj.fail(400)
                }
            } else {
                /**
                 * If there are no arguments, just execute the method
                 */
                m.invoke(controllerObj)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error(
                    e.message ?: "${resourceTable.url} has error occurred, but the error message could not be obtained "
            )
            context.fail(500)
        }
    }

    /**
     * Converts an http argument to the same type as a method argument
     * @param paraName String
     * @param para Parameter
     * @param controllerObj Resource
     * @return Any?
     */
    private fun getParaByType(paraName: String, para: java.lang.reflect.Parameter, controllerObj: Resource): Any? {

        val jsonObject = JSONObject(controllerObj.getParams())
        if (jsonObject[paraName] == null) {
            jsonObject[paraName] = para.getAnnotation(Parameter::class.java).defaultValue
        }
        when (para.type.typeName) {
            "java.lang.String" -> return jsonObject.getString(paraName)
            "kotlin.String" -> return jsonObject.getString(paraName)
            "int" -> return jsonObject.getIntValue(paraName)
            "double" -> return jsonObject.getDoubleValue(paraName)
            "float" -> return jsonObject.getFloatValue(paraName)
            "long" -> return jsonObject.getLongValue(paraName)
            "short" -> return jsonObject.getShort(paraName)
            "java.math.BigDecimal" -> return jsonObject.getBigDecimal(paraName)
            "java.math.BigInteger" -> return jsonObject.getBigInteger(paraName)
            "java.util.Date" -> return jsonObject.getDate(paraName)
            "java.sql.Timestamp" -> return jsonObject.getTimestamp(paraName)
        }
        return null
    }

}