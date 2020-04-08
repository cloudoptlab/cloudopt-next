/*
 * Copyright 2017 Cloudopt.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package net.cloudopt.next.web

import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.*
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import net.cloudopt.next.json.Jsoner
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.utils.Beaner
import net.cloudopt.next.utils.Classer
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.web.route.SocketJS


/*
 * @author: Cloudopt
 * @Time: 2018/1/17
 * @Description: Cloudopt Next Server Verticle
 */
class CloudoptServerVerticle : AbstractVerticle() {

    val logger = Logger.getLogger(CloudoptServerVerticle::class.java)

    override fun start() {

        CloudoptServer.scan()

        //Register plugins
        CloudoptServer.plugins.forEach { plugin ->
            if (plugin.start()) {
                CloudoptServer.logger.info("[PLUGIN] Registered plugin：" + plugin.javaClass.name)
            } else {
                CloudoptServer.logger.info("[PLUGIN] Started plugin was error：" + plugin.javaClass.name)
            }
        }

        val server = CloudoptServer.vertx.createHttpServer(ConfigManager.config.vertxHttpServer)

        val router = Router.router(CloudoptServer.vertx)

        // Print Baner
        Banner.print()

        // Set json provider
        Jsoner.jsonProvider = Beaner.newInstance(Classer.loadClass(ConfigManager.config.jsonProvider))

        // Register websockets
        if (CloudoptServer.sockets.size > 0){
            val sockJSHandler = SockJSHandler.create(CloudoptServer.vertx, ConfigManager.config.socket)
            CloudoptServer.sockets.forEach { clazz ->
                val websocketAnnotation: SocketJS? = clazz.getDeclaredAnnotation(SocketJS::class.java)
                sockJSHandler.socketHandler { sockJSHandler->
                    var handler = Beaner.newInstance<SocketJSResource>(clazz)
                    handler.handler(sockJSHandler)
                }
                if (!websocketAnnotation?.value?.endsWith("/*")!!){
                    logger.error("[SOCKET] Url must be end with /* !")
                }
                logger.info("[SOCKET] Registered socket resource: ${websocketAnnotation?.value} -> ${clazz.name}")
                router.route(websocketAnnotation?.value).handler(sockJSHandler)
            }
        }

        //The ResponseContentTypeHandler can set the Content-Type header automatically.
        router.route("/*").handler(ResponseContentTypeHandler.create())

        router.route().handler(BodyHandler.create())

        router.route().handler(BodyHandler.create().setBodyLimit(ConfigManager.config.bodyLimit))

        //Set timeout
        router.route("/*").handler(TimeoutHandler.create(ConfigManager.config.timeout))

        // Set csrf
        if (ConfigManager.config.waf.csrf) {
            router.route("/*").handler(CSRFHandler.create(ConfigManager.config.waf.encryption))
        }

        // Register failure handler
        CloudoptServer.logger.info("[FAILURE HANDLER] Registered failure handler：${CloudoptServer.errorHandler::class.java.getName()}")

        router.route("/*").failureHandler { failureRoutingContext ->
            CloudoptServer.errorHandler.init(failureRoutingContext)
            CloudoptServer.errorHandler.handle()
            logger.error(failureRoutingContext.failure().toString())

        }

        //Register handlers
        CloudoptServer.handlers.forEach { handler ->
            CloudoptServer.logger.info("[HANDLER] Registered handler：${handler::class.java.getName()}")
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

        //Register interceptors
        CloudoptServer.interceptors.forEach { url, clazz ->
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

        //Register validators
        CloudoptServer.validators.forEach { url, map ->
            map.keys.forEach { key ->
                val validatorList = map.get(key)
                validatorList?.forEach { validator ->
                    router.route(key, url).handler { context ->
                        try {
                            val v = Beaner.newInstance<Validator>(validator?.java!!)
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
                context.response().end(Welcomer.html())
            }
        }

        // Register method
        CloudoptServer.controllers.forEach { resourceTable ->
            if (resourceTable.blocking) {
                router.route(resourceTable.httpMethod, resourceTable.url).blockingHandler { context ->
                    try {
                        val controllerObj = Beaner.newInstance<Resource>(resourceTable.clazz)
                        controllerObj.init(context)
                        val m = resourceTable.clazz.getDeclaredMethod(resourceTable.methodName)
                        m.invoke(controllerObj)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        context.response().end()
                    }
                }
            } else {
                router.route(resourceTable.httpMethod, resourceTable.url).handler { context ->
                    try {
                        val controllerObj = Beaner.newInstance<Resource>(resourceTable.clazz)
                        controllerObj.init(context)
                        val m = resourceTable.clazz.getDeclaredMethod(resourceTable.methodName)
                        m.invoke(controllerObj)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        context.response().end()
                    }
                }
            }

            CloudoptServer.logger.info(
                "[RESOURCE] Registered resource :${resourceTable.methodName} | ${resourceTable.url}"
            )
        }

        server.requestHandler { router.accept(it) }.listen(ConfigManager.config.port) { result ->
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
        CloudoptServer.plugins.forEach { plugin ->
            if (!plugin.stop()) {
                CloudoptServer.logger.info("[PLUGIN] Stoped plugin was error：${plugin.javaClass.name}")
            }
        }
    }

}