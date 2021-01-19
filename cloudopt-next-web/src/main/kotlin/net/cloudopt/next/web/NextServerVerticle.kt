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
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.*
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.validator.ValidatorTool
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.web.event.AfterEvent
import net.cloudopt.next.web.event.EventManager
import net.cloudopt.next.web.handler.ErrorHandler
import net.cloudopt.next.web.route.Parameter
import net.cloudopt.next.web.route.RequestBody
import net.cloudopt.next.web.route.SocketJS
import net.cloudopt.next.web.route.WebSocket
import kotlin.reflect.KParameter
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName


/*
 * @author: Cloudopt
 * @Time: 2018/1/17
 * @Description: Cloudopt Next Server Verticle
 */
class NextServerVerticle : CoroutineVerticle() {

    val logger = Logger.getLogger(NextServerVerticle::class.java)

    override suspend fun start() {

        val server = Worker.vertx.createHttpServer(ConfigManager.config.vertxHttpServer)

        val router = Router.router(Worker.vertx)


        /**
         * Register sockJS
         */
        if (NextServer.sockJSes.size > 0) {
            val sockJSHandler = SockJSHandler.create(Worker.vertx, ConfigManager.config.socket)
            NextServer.sockJSes.forEach { clazz ->
                val socketAnnotation: SocketJS? = clazz.findAnnotation<SocketJS>()
                sockJSHandler.socketHandler { sockJSHandler ->
                    val handler = clazz.createInstance<SockJSResource>()
                    handler.handler(sockJSHandler)
                }
                if (!socketAnnotation?.value?.endsWith("/*")!!) {
                    logger.error("[SOCKET] Url must be end with /* !")
                }
                logger.info("[SOCKET] Registered socket resource: ${socketAnnotation.value} -> ${clazz.jvmName}")
                router.route(socketAnnotation.value).handler(sockJSHandler)
            }
        }

        /**
         * Register websocket
         */
        if (NextServer.webSockets.size > 0) {
            NextServer.webSockets.forEach { clazz ->
                val websocketAnnotation: WebSocket? = clazz.findAnnotation<WebSocket>()
                router.route(websocketAnnotation?.value).handler { context ->
                    try {

                        val controllerObj = clazz.createInstance<WebSocketResource>()
                        if (controllerObj.beforeConnection(Resource().init(context))) {
                            val userWebSocketConnection = context.request().toWebSocket()
                            userWebSocketConnection.onComplete {
                                controllerObj.onConnectionComplete(userWebSocketConnection.result())
                            }
                            /**
                             * Automatically register methods in websocket routing.
                             */
                            userWebSocketConnection.onSuccess {
                                var userWebSocketConnectionResult = userWebSocketConnection.result()
                                controllerObj.onConnectionSuccess(userWebSocketConnectionResult)

                                userWebSocketConnectionResult.frameHandler { frame ->
                                    controllerObj.onFrameMessage(frame, userWebSocketConnectionResult)
                                }
                                userWebSocketConnectionResult.textMessageHandler { text ->
                                    controllerObj.onTextMessage(text, userWebSocketConnectionResult)
                                }
                                userWebSocketConnectionResult.binaryMessageHandler { binary ->
                                    controllerObj.onBinaryMessage(binary, userWebSocketConnectionResult)
                                }
                                userWebSocketConnectionResult.pongHandler { buffer ->
                                    controllerObj.onPingPong(buffer, userWebSocketConnectionResult)
                                }
                                userWebSocketConnectionResult.exceptionHandler { throwable ->
                                    controllerObj.onException(throwable, userWebSocketConnectionResult)
                                }
                                userWebSocketConnectionResult.drainHandler {
                                    controllerObj.onDrain(userWebSocketConnectionResult)
                                }
                                userWebSocketConnectionResult.endHandler {
                                    controllerObj.onEnd(userWebSocketConnectionResult)
                                }
                            }
                            userWebSocketConnection.onFailure {
                                controllerObj.onConnectionFailure(userWebSocketConnection.cause())
                            }
                        }

                    } catch (e: InstantiationException) {
                        e.printStackTrace()
                        context.response().end()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                        context.response().end()
                    }
                }
                logger.info("[WEBSOCKET] Registered socket resource: ${websocketAnnotation?.value} -> ${clazz.jvmName}")

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
            router.route("/*").handler(CSRFHandler.create(vertx, ConfigManager.config.waf.encryption))
        }

        /**
         * Register failure handler
         */
        NextServer.logger.info("[FAILURE HANDLER] Registered failure handler：${ConfigManager.config.errorHandler}")

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
                launch {
                    val resource = Resource()
                    resource.init(context)
                    val interceptors = clazz.map { it.createInstance() }

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
        }

        /**
         * Register validators
         */
        NextServer.validators.forEach { (url, map) ->
            map.keys.forEach { key ->
                val validatorList = map[key]
                validatorList?.forEach { validator ->
                    router.route(key, url).handler { context ->
                        launch {
                            try {
                                val v = validator.createInstance()
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
                    launch {
                        requestProcessing(resourceTable, context)
                    }
                }
            } else {
                router.route(resourceTable.httpMethod, resourceTable.url).handler { context ->
                    launch {
                        requestProcessing(resourceTable, context)
                    }
                }
            }

            NextServer.logger.info(
                "[RESOURCE] Registered resource ${resourceTable.httpMethod} :${resourceTable.methodName} | ${resourceTable.url}"
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

    override suspend fun stop() {
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
        val errorHandler = NextServer.errorHandler.createInstance()
        errorHandler.init(context)
        errorHandler.handle()
        if (context.failure() != null) {
            context.failure().printStackTrace()
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
    private suspend fun requestProcessing(resourceTable: ResourceTable, context: RoutingContext) {
        try {
            val controllerObj = resourceTable.clazz.createInstance()
            controllerObj.init(context)

            if (NextServer.handlers.isNotEmpty() || resourceTable.clazzMethod.hasAnnotation<AfterEvent>()) {
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
                    if (resourceTable.clazzMethod.hasAnnotation<AfterEvent>()) {
                        val afterEvent =
                            resourceTable.clazzMethod.findAnnotation<AfterEvent>() ?: AfterEvent::class.createInstance()
                        for (topic in afterEvent.value) {
                            EventManager.sendObject(topic, context.data(), "map")
                        }
                    }
                }
            }

            /**
             * If the method supports parameter injection, it will automatically extract the corresponding parameter
             * and inject it.When using kotlin's reflection calls to get the parameters of a function, if the function
             * needs to be instantiated first, then it will take an INSTANCE parameter by default.
             *
             */
            if (resourceTable.clazzMethod.parameters.isEmpty() ||
                (resourceTable.clazzMethod.parameters.size == 1 &&
                        resourceTable.clazzMethod.parameters[0].kind.name == "INSTANCE")
            ) {
                /**
                 * If there are no arguments, just execute the method
                 */
                if (resourceTable.clazzMethod.isSuspend) {
                    resourceTable.clazzMethod.callSuspend(controllerObj)
                } else {
                    resourceTable.clazzMethod.call(controllerObj)
                }


            } else {
                val arr = mutableMapOf<KParameter, Any?>()
                val jsonObject: JSONObject = JSONObject.toJSON(controllerObj.getParams()) as JSONObject
                for (para in resourceTable.clazzMethod.parameters) {
                    if (para.kind.name == "VALUE" && para.hasAnnotation<Parameter>()) {
                        getParaByType(para.findAnnotation<Parameter>()?.value, para, jsonObject)?.let {
                            arr.put(
                                para,
                                it
                            )
                        }
                    }
                    if (para.hasAnnotation<RequestBody>()) {
                        controllerObj.getBodyJson(para.type.jvmErasure)?.let { arr.put(para, it) }
                    }
                }
                /**
                 * Support for verifying injected parameters
                 * @see ValidatorTool
                 */
                val validatorResult =
                    ValidatorTool.validateParameters(controllerObj, resourceTable.clazzMethod, arr)
                if (validatorResult.result) {
                    arr[resourceTable.clazzMethod.parameters[0]] = controllerObj
                    if (resourceTable.clazzMethod.isSuspend) {
                        resourceTable.clazzMethod.callSuspendBy(arr)
                    } else {
                        resourceTable.clazzMethod.callBy(arr)
                    }

                } else {
                    controllerObj.context.put("errorMessage", validatorResult.message)
                    controllerObj.fail(400)
                }
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
     * @param jsonObject The json object after all parameters are formatted
     * @return Any?
     */
    private fun getParaByType(
        paraName: String?,
        para: KParameter,
        jsonObject: JSONObject
    ): Any? {
        var finalParaName = if (paraName.isNullOrBlank()) {
            para.name
        } else {
            paraName
        }
        if (jsonObject[finalParaName] == null && para.findAnnotation<Parameter>()?.defaultValue?.isNotBlank() == true
        ) {
            jsonObject[finalParaName] = para.findAnnotation<Parameter>()?.defaultValue
        }
        when (para.type.jvmErasure.jvmName) {
            "java.lang.String" -> return jsonObject.getString(finalParaName)
            "kotlin.String" -> return jsonObject.getString(finalParaName)
            "int" -> return jsonObject.getIntValue(finalParaName)
            "double" -> return jsonObject.getDoubleValue(finalParaName)
            "float" -> return jsonObject.getFloatValue(finalParaName)
            "long" -> return jsonObject.getLongValue(finalParaName)
            "short" -> return jsonObject.getShort(finalParaName)
            "java.math.BigDecimal" -> return jsonObject.getBigDecimal(finalParaName)
            "java.math.BigInteger" -> return jsonObject.getBigInteger(finalParaName)
            "java.util.Date" -> return jsonObject.getDate(finalParaName)
            "java.sql.Timestamp" -> return jsonObject.getTimestamp(finalParaName)
        }
        return null
    }

}