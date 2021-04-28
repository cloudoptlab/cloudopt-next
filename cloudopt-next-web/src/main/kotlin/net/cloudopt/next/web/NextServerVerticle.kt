/*
 * Copyright 2017-2021 Cloudopt
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

import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.*
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch
import net.cloudopt.next.core.Worker
import net.cloudopt.next.json.Jsoner.toJsonObject
import net.cloudopt.next.json.Jsoner.toJsonString
import net.cloudopt.next.logging.test.Logger
import net.cloudopt.next.validator.ValidatorTool
import net.cloudopt.next.waf.Wafer
import net.cloudopt.next.web.annotation.After
import net.cloudopt.next.web.annotation.Before
import net.cloudopt.next.web.handler.ErrorHandler
import net.cloudopt.next.web.annotation.Parameter
import net.cloudopt.next.web.annotation.RequestBody
import net.cloudopt.next.web.annotation.SocketJS
import net.cloudopt.next.web.annotation.WebSocket
import java.lang.IllegalArgumentException
import java.sql.Timestamp
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.reflect.KParameter
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

class NextServerVerticle : CoroutineVerticle() {

    val logger = Logger.getLogger(NextServerVerticle::class)

    override suspend fun start() {

        val server = Worker.vertx.createHttpServer(NextServer.webConfig.httpServerOptions)

        val router = Router.router(Worker.vertx)


        /**
         * Register sockJS
         */
        if (NextServer.sockJSes.size > 0) {
            val sockJSHandler = SockJSHandler.create(Worker.vertx, NextServer.webConfig.socket)
            NextServer.sockJSes.forEach { clazz ->
                val socketAnnotation: SocketJS? = clazz.findAnnotation()
                sockJSHandler.socketHandler { sockJSHandler ->
                    val handler = clazz.createInstance()
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
                val websocketAnnotation: WebSocket? = clazz.findAnnotation()
                router.route(websocketAnnotation?.value).handler { context ->
                    try {

                        val controllerObj = clazz.createInstance()
                        if (controllerObj.beforeConnection(Resource().init(context))) {
                            val userWebSocketConnection = context.request().toWebSocket()
                            userWebSocketConnection.onComplete {
                                controllerObj.onConnectionComplete(userWebSocketConnection.result())
                            }
                            /**
                             * Automatically register methods in websocket routing.
                             */
                            userWebSocketConnection.onSuccess {
                                val userWebSocketConnectionResult = userWebSocketConnection.result()
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

        router.route("/*").handler(BodyHandler.create().setBodyLimit(NextServer.webConfig.bodyLimit))

        /**
         * Set timeout
         */
        router.route("/*").handler(TimeoutHandler.create(NextServer.webConfig.timeout))

        /**
         * Set csrf
         */
        if (Wafer.config.csrf) {
            router.route("/*").handler(CSRFHandler.create(vertx, Wafer.config.encryption))
        }

        /**
         * Register failure handler
         */
        NextServer.logger.info("[FAILURE HANDLER] Registered failure handler：${NextServer.webConfig.errorHandler}")

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
                } catch (e: Exception) {
                    e.printStackTrace()
                    Resource().init(context).fail(500)
                }
            }
        }

        router.route("/" + NextServer.webConfig.staticPackage + "/*").handler(
            StaticHandler.create().setIndexPage(NextServer.webConfig.indexPage)
                .setIncludeHidden(false).setWebRoot(NextServer.webConfig.staticPackage)
        )

        /**
         * Register interceptors
         */
        NextServer.interceptors.forEach { (url, clazz) ->
            router.route(url).handler { context ->
                val resource = Resource()
                resource.init(context)
                launch {
                    try {
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
                    } catch (e: Exception) {
                        resource.fail(500)
                    }
                }
            }
        }

        /**
         * Automatically check whether the method annotation contains an @Before annotation, and if so,
         * automatically execute the method specified in the annotation that needs to be executed.
         */
        NextServer.beforeRouteHandlersTable.forEach { (url, map) ->
            map.keys.forEach { key ->
                val beforeRouteHandlerList = map[key]
                beforeRouteHandlerList?.forEach { beforeRouteHandler ->
                    router.route(key, url).handler { context ->
                        val resource = Resource()
                        resource.init(context)
                        launch {
                            try {
                                val before: Before? = beforeRouteHandler.annotationClass.findAnnotation()
                                var invokeResult = true
                                for (invoker in before?.invokeBy!!) {
                                    val routeHandlerInstance: RouteHandler = invoker.createInstance()
                                    if (!routeHandlerInstance.handle(beforeRouteHandler, resource)) {
                                        invokeResult = false
                                        break
                                    }
                                }
                                if (invokeResult){
                                    context.next()
                                }else{
                                    return@launch
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                resource.fail(500)
                            }
                        }
                    }
                }

            }
        }

        if (NextServer.resourceTable.size < 1) {
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
        NextServer.resourceTable.forEach { resourceTable ->
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

        server.requestHandler(router).listen(NextServer.webConfig.port) { result ->
            if (result.succeeded()) {
                NextServer.logger.info(
                    "=========================================================================================================="
                )
                NextServer.logger.info("\uD83D\uDC0B Cloudopt Next started success!")
                if (NextServer.webConfig.httpServerOptions.isSsl) {
                    NextServer.logger.info("https://127.0.0.1:${NextServer.webConfig.port}")
                } else {
                    NextServer.logger.info("http://127.0.0.1:${NextServer.webConfig.port}")
                }
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
    }

    /**
     * is used to process normal http requests, automatically generating new objects from the resource class of the
     * route and calls its invoke method. It also injects parameters depending on whether the method corresponding
     * to the route contains a parameter injection annotation or not.
     * @param resourceTable ResourceTable
     * @param context RoutingContext
     * @see ResourceTable
     * @see RoutingContext
     */
    private suspend fun requestProcessing(resourceTable: ResourceTable, context: RoutingContext) {
        val resource:Resource = resourceTable.clazz.createInstance()
        resource.init(context)
        try {
            context.response().endHandler {
                /**
                 * Executes a block handler that is called at the end of the route
                 */
                NextServer.handlers.forEach { handler ->
                    handler.afterCompletion(Resource().init(context))
                }
                /**
                 * Automatically check whether the method annotation contains an @After annotation, and if so,
                 * automatically execute the method specified in the annotation that needs to be executed.
                 */
                NextServer.afterRouteHandlersTable[resourceTable.url]?.get(resourceTable.httpMethod)?.forEach { it ->
                    val after: After = it.annotationClass.findAnnotation()!!
                    launch {
                        for (invoker in after.invokeBy) {
                            val routeHandlerInstance: RouteHandler = invoker.createInstance()
                            if (!routeHandlerInstance.handle(it, resource)) {
                                break
                            }
                        }
                        if (!context.response().closed()){
                            context.response().end()
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
                resourceTable.clazzMethod.callSuspend(resource)


            } else {
                val arr = mutableMapOf<KParameter, Any?>()
                val jsonObject = resource.getParams().toJsonString().toJsonObject()
                for (para in resourceTable.clazzMethod.parameters) {
                    if (para.kind.name == "VALUE" && para.hasAnnotation<Parameter>()) {
                        try {
                            arr[para] = getParaByType(para.findAnnotation<Parameter>()?.value ?: "", para, jsonObject)
                        } catch (e: IllegalArgumentException) {
                            resource.fail(400)
                            e.printStackTrace()
                            return
                        }

                    }
                    if (para.hasAnnotation<RequestBody>()) {
                        try {
                            arr[para] = resource.getBodyJson(para.type.jvmErasure)
                        } catch (e: NullPointerException) {
                            resource.fail(400)
                            e.printStackTrace()
                            return
                        } catch (e: IllegalArgumentException) {
                            resource.fail(400)
                            e.printStackTrace()
                            return
                        }
                    }
                }
                /**
                 * Support for verifying injected parameters
                 * @see ValidatorTool
                 */
                val validatorResult =
                    ValidatorTool.validateParameters(resource, resourceTable.clazzMethod, arr)
                if (validatorResult.result) {
                    arr[resourceTable.clazzMethod.parameters[0]] = resource
                    resourceTable.clazzMethod.callSuspendBy(arr)
                } else {
                    resource.context.put("errorMessage", validatorResult.message)
                    resource.fail(400)
                    return
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error(
                e.message ?: "${resourceTable.url} has error occurred, but the error message could not be obtained "
            )
            resource.fail(500)
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
        paraName: String,
        para: KParameter,
        jsonObject: JsonObject
    ): Any? {
        val finalParaName = paraName.ifBlank {
            para.name
        }
        if (jsonObject.containsKey(finalParaName) && jsonObject.getString(finalParaName ?: "").isNotBlank()) {
            when (para.type.jvmErasure) {
                String::class ->
                    return jsonObject.getString(finalParaName)
                String::class.starProjectedType.withNullability(true) ->
                    return jsonObject.getString(finalParaName)

                Int::class ->
                    return jsonObject.getString(finalParaName).toInt()
                Int::class.starProjectedType.withNullability(true) ->
                    return jsonObject.getString(finalParaName).toIntOrNull()

                Double::class ->
                    return jsonObject.getString(finalParaName).toDouble()
                Double::class.starProjectedType.withNullability(true) ->
                    return jsonObject.getString(finalParaName).toDoubleOrNull()

                Float::class ->
                    return jsonObject.getString(finalParaName).toFloat()
                Float::class.starProjectedType.withNullability(true) ->
                    return jsonObject.getString(finalParaName).toFloatOrNull()

                Short::class ->
                    return jsonObject.getString(finalParaName).toShortOrNull()
                Short::class.starProjectedType.withNullability(true) ->
                    return jsonObject.getString(finalParaName).toShortOrNull()

                Long::class ->
                    return jsonObject.getString(finalParaName).toLong()
                Long::class.starProjectedType.withNullability(true) ->
                    return jsonObject.getString(finalParaName).toLongOrNull()

                java.math.BigDecimal::class ->
                    return jsonObject.getString(finalParaName).toBigDecimal()
                java.math.BigDecimal::class.starProjectedType.withNullability(true) ->
                    return jsonObject.getString(finalParaName).toBigDecimalOrNull()

                java.math.BigInteger::class ->
                    return jsonObject.getString(finalParaName).toBigInteger()
                java.math.BigInteger::class.starProjectedType.withNullability(true) ->
                    return jsonObject.getString(finalParaName).toBigIntegerOrNull()

                java.util.Date::class ->
                    return DateFormat.getDateInstance().parse(jsonObject.getString(finalParaName))
                java.util.Date::class.starProjectedType.withNullability(true) ->
                    return DateFormat.getDateInstance().parse(jsonObject.getString(finalParaName))

                java.sql.Timestamp::class ->
                    return Timestamp.valueOf(jsonObject.getString(finalParaName))
                java.sql.Timestamp::class.starProjectedType.withNullability(true) ->
                    return Timestamp.valueOf(jsonObject.getString(finalParaName))

                java.time.LocalDateTime::class ->
                    return LocalDateTime.parse(jsonObject.getString(finalParaName))
                java.time.LocalDateTime::class.starProjectedType.withNullability(true) ->
                    return LocalDateTime.parse(jsonObject.getString(finalParaName))

                java.time.LocalDate::class ->
                    return LocalDate.parse(jsonObject.getString(finalParaName))
                java.time.LocalDate::class.starProjectedType.withNullability(true) ->
                    return LocalDate.parse(jsonObject.getString(finalParaName))

            }
        }
        return null
    }

}