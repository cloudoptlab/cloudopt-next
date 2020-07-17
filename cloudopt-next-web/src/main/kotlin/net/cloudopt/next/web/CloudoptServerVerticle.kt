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
import net.cloudopt.next.json.Jsoner
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.utils.Beaner
import net.cloudopt.next.utils.Classer
import net.cloudopt.next.validator.ValidatorTool
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.web.event.AfterEvent
import net.cloudopt.next.web.event.EventManager
import net.cloudopt.next.web.handler.ErrorHandler
import net.cloudopt.next.web.route.Parameter
import net.cloudopt.next.web.route.RequestBody
import net.cloudopt.next.web.route.SocketJS


/*
 * @author: Cloudopt
 * @Time: 2018/1/17
 * @Description: Cloudopt Next Server Verticle
 */
class CloudoptServerVerticle : AbstractVerticle() {

    val logger = Logger.getLogger(CloudoptServerVerticle::class.java)

    override fun start() {

        val server = CloudoptServer.vertx.createHttpServer(ConfigManager.config.vertxHttpServer)

        val router = Router.router(CloudoptServer.vertx)


        /**
         * Register sockJS
         */
        if (CloudoptServer.sockets.size > 0) {
            val sockJSHandler = SockJSHandler.create(CloudoptServer.vertx, ConfigManager.config.socket)
            CloudoptServer.sockets.forEach { clazz ->
                val websocketAnnotation: SocketJS? = clazz.getDeclaredAnnotation(SocketJS::class.java)
                sockJSHandler.socketHandler { sockJSHandler ->
                    val handler = Beaner.newInstance<SockJSResource>(clazz)
                    handler.handler(sockJSHandler)
                }
                if (!websocketAnnotation?.value?.endsWith("/*")!!) {
                    logger.error("[SOCKET] Url must be end with /* !")
                }
                logger.info("[SOCKET] Registered socket resource: ${websocketAnnotation.value} -> ${clazz.name}")
                router.route(websocketAnnotation.value).handler(sockJSHandler)
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
        CloudoptServer.logger.info("[FAILURE HANDLER] Registered failure handler：${CloudoptServer.errorHandler::class.java.name}")

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
        CloudoptServer.handlers.forEach { handler ->
            CloudoptServer.logger.info("[HANDLER] Registered handler：${handler::class.java.name}")
            router.route("/*").handler { context ->
                try {
                    handler.preHandle(Resource().init(context))
                    context.next()
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
        CloudoptServer.interceptors.forEach { (url, clazz) ->
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
        CloudoptServer.validators.forEach { (url, map) ->
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

        if (CloudoptServer.controllers.size < 1) {
            router.route("/").blockingHandler { context ->
                context.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")
                context.response().end(Welcomer.home())
            }
        }

        /**
         * Register method
         */
        CloudoptServer.controllers.forEach { resourceTable ->
            if (resourceTable.blocking) {
                router.route(resourceTable.httpMethod, resourceTable.url).blockingHandler { context ->
                    requestProcessing(resourceTable, context)
                }
            } else {
                router.route(resourceTable.httpMethod, resourceTable.url).handler { context ->
                    requestProcessing(resourceTable, context)
                }
            }

            CloudoptServer.logger.info(
                "[RESOURCE] Registered resource :${resourceTable.methodName} | ${resourceTable.url}"
            )
        }

        server.requestHandler(router).listen(ConfigManager.config.port) { result ->
            if (result.succeeded()) {
                CloudoptServer.logger.info(
                    "=========================================================================================================="
                )
                CloudoptServer.logger.info("\uD83D\uDC0B Cloudopt Next started success!")
                CloudoptServer.logger.info("http://127.0.0.1:${ConfigManager.config.port}")
                CloudoptServer.logger.info(
                    "=========================================================================================================="
                )

            } else {
                CloudoptServer.logger.error(
                    "=========================================================================================================="
                )
                CloudoptServer.logger.error("\uD83D\uDC0B Cloudopt Next started error! ${result.cause()}")
                CloudoptServer.logger.error(
                    "=========================================================================================================="
                )
            }
        }
    }

    override fun stop() {
    }

    private fun errorProcessing(context: RoutingContext) {
        val errorHandler = Beaner.newInstance<ErrorHandler>(CloudoptServer.errorHandler)
        errorHandler.init(context)
        errorHandler.handle()
        if (context.failure() != null) {
            logger.error(context.failure().toString())
        }
        if (!errorHandler.response.ended()) {
            errorHandler.end()
        }
    }

    private fun requestProcessing(resourceTable: ResourceTable, context: RoutingContext) {
        try {
            val controllerObj = Beaner.newInstance<Resource>(resourceTable.clazz)
            controllerObj.init(context)
            val m = if (resourceTable.parameterTypes.isNotEmpty()) {
                resourceTable.clazz.getDeclaredMethod(resourceTable.methodName, *resourceTable.parameterTypes)
            } else {
                resourceTable.clazz.getDeclaredMethod(resourceTable.methodName)
            }
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
                val validatorResult = ValidatorTool.validateParameters(controllerObj,m,arr.toArray())
                if (validatorResult.result){
                    m.invoke(controllerObj, *arr.toArray())
                }else{
                    controllerObj.context.put("errorMessage",validatorResult.message)
                    controllerObj.fail(400)
                }
            } else {
                m.invoke(controllerObj)
            }
            // Run after event
            if (m.getAnnotation(AfterEvent::class.java) != null && context.response().ended()) {
                val afterEvent = m.getAnnotation(AfterEvent::class.java)
                for (topic in afterEvent.value) {
                    EventManager.sendObject(topic, context.data()?: mutableMapOf<String, Any>())
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