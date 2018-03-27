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
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.*
import net.cloudopt.next.aop.Beaner
import net.cloudopt.next.web.config.ConfigManager

/*
 * @author: Cloudopt
 * @Time: 2018/1/17
 * @Description: Cloudopt Next Server Verticle
 */
class CloudoptServerVerticle : AbstractVerticle() {

    override fun start() {

        //Register plugins
        CloudoptServer.plugins.forEach { plugin ->
            if (plugin.start()) {
                CloudoptServer.logger.info("[PLUGIN] Registered plugin：" + plugin.javaClass.name)
            } else {
                CloudoptServer.logger.info("[PLUGIN] Started plugin was error：" + plugin.javaClass.name)
            }
        }

        val server = CloudoptServer.vertx.createHttpServer(CloudoptServer.httpServerOptions)

        val router = Router.router(CloudoptServer.vertx)

        Banner.print()

        //The ResponseContentTypeHandler can set the Content-Type header automatically.
        router.route("/*").handler(ResponseContentTypeHandler.create())

        router.route().handler(BodyHandler.create())

        router.route().handler(CookieHandler.create())

        router.route().handler(BodyHandler.create().setBodyLimit(ConfigManager.webConfig.bodyLimit))

        //Set timeout
        router.route("/*").handler(TimeoutHandler.create(ConfigManager.webConfig.timeout))

        //Set csrf
        if (ConfigManager.wafConfig.csrf) {
            router.route("/*").handler(CSRFHandler.create("cloudopt-next"))
        }

        //Register failure handler
        CloudoptServer.logger.info("[FAILURE HANDLER] Registered failure handler：" + CloudoptServer.errorHandler::class.java.getName())

        router.route("/*").failureHandler { failureRoutingContext ->
            CloudoptServer.errorHandler.init(failureRoutingContext)
            CloudoptServer.errorHandler.handle()
        }

        //Register handlers
        CloudoptServer.handlers.forEach { handler ->
            CloudoptServer.logger.info("[HANDLER] Registered handler：" + handler::class.java.getName())
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

        router.route("/" + ConfigManager.webConfig.staticPackage + "/*").handler(StaticHandler.create().setIndexPage(ConfigManager.webConfig.indexPage)
                .setIncludeHidden(false).setWebRoot(ConfigManager.webConfig.staticPackage))

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
                router.route(key, url).handler { context ->
                    try {
                        val v = Beaner.newInstance<Validator>(map.get(key)?.java!!)
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

        //Register method
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

            CloudoptServer.logger.info("[RESOURCE] Registered Resource :" + resourceTable.methodName + " | "
                    + resourceTable.url)
        }

        if (CloudoptServer.controllers.size == 0) {
            router.route(HttpMethod.GET, "/*").handler { context ->
                val resource = Resource()
                resource.init(context)
                resource.renderText("A first cloudopt next application!")
            }
        }

        server.requestHandler({ router.accept(it) }).listen(ConfigManager.webConfig.port) { result ->
            if (result.succeeded()) {
                CloudoptServer.logger.info(
                        "==========================================================================================================")
                CloudoptServer.logger.info("Cloudopt Next started is success!")
                CloudoptServer.logger.info(
                        "==========================================================================================================")

            } else {
                CloudoptServer.logger.error(
                        "==========================================================================================================")
                CloudoptServer.logger.error("Cloudopt Next started is error! " + result.cause())
                CloudoptServer.logger.error(
                        "==========================================================================================================")
            }
        }
    }

    override fun stop() {
        CloudoptServer.plugins.forEach { plugin ->
            if (!plugin.stop()) {
                CloudoptServer.logger.info("[PLUGIN] Stoped plugin was error：" + plugin.javaClass.name)
            }
        }
    }

}