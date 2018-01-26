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
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.handler.ResponseContentTypeHandler
import io.vertx.ext.web.handler.StaticHandler
import net.cloudopt.next.aop.Beaner
import net.cloudopt.next.aop.Classer
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.web.annotation.*
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.web.handler.Handler
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass


/*
 * @author: Cloudopt
 * @Time: 2018/1/17
 * @Description: Cloudopt Next Server Verticle
 */
class CloudoptServerVerticle : AbstractVerticle() {

    private val logger = Logger.getLogger(CloudoptServer.javaClass)

    private val errorHandler = Beaner.newInstance<Handler>(Classer.loadClass(ConfigManager.webConfig.errorHandler))


    override fun start() {

        val server = vertx.createHttpServer()

        val router = Router.router(vertx)

        Banner.print()

        //The ResponseContentTypeHandler can set the Content-Type header automatically.
        router.route("/*").handler(ResponseContentTypeHandler.create())

        router.route().handler(BodyHandler.create())

        router.route().handler(CookieHandler.create())

        router.route().handler(BodyHandler.create().setBodyLimit(ConfigManager.webConfig.bodyLimit))

        //Register failure handler
        logger.info("Registered failure handler：" + errorHandler::class.java.getName())

        router.get().failureHandler { failureRoutingContext ->
            errorHandler.init(failureRoutingContext)
        }

        //Register handlers
        CloudoptServer.handlers.forEach { handler ->
            logger.info("Registered handler：" + handler::class.java.getName())
            router.route("/*").blockingHandler { context ->
                try {
                    handler.init(context)
                    handler.handle()
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                    context.response().end()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                    context.response().end()
                }
            }
        }

        //Register plugins
        CloudoptServer.plugins.forEach { plugin ->
            logger.info("Registered plugin：" + plugin.javaClass.name)
            plugin.start()
        }

        //Register exception routes
        ConfigManager.webConfig.exclusions.split(";").forEach { exclusion ->
            logger.info("Registered exception routes：" + exclusion)
            router.route(exclusion).blockingHandler(StaticHandler.create().setIndexPage(ConfigManager.webConfig.indexPage)
                    .setIncludeHidden(false).setWebRoot(ConfigManager.webConfig.webroot))
        }

        //Register interceptors
        CloudoptServer.interceptors.forEach { url, interceptor ->
            router.route(url).blockingHandler { context ->
                val resource = Resource()
                resource.init(context)
                if (interceptor.intercept(resource)) {
                    context.next()
                } else {
                    interceptor.response(resource).response?.end()
                }
            }
        }

        //Register validators
        CloudoptServer.validators.forEach { url, map ->
            map.keys.forEach { key ->
                router.route(key, url).blockingHandler { context ->
                    try {
                        var v = Beaner.newInstance<Validator>(map.get(key)!!)
                        var resource = Resource()
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

            var controllerObj = Beaner.newInstance<Resource>(resourceTable.clazz::class.java)

            router.route(resourceTable.httpMethod, resourceTable.url).blockingHandler({ context ->
                try {
                    controllerObj.init(context)
                    var m = resourceTable.clazz::class.java.getDeclaredMethod(resourceTable.methodName)
                    m.invoke(controllerObj)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                    context.response().end()
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                    context.response().end()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                    context.response().end()
                }
            })

            logger.info("Registered Resource :" + resourceTable.methodName + " | "
                    + resourceTable.url)
        }

        if (CloudoptServer.controllers.size == 0){
            router.route(HttpMethod.GET, "/*").blockingHandler({ context ->
                var resource = Resource()
                resource.init(context)
                resource.renderText("A first cloudopt next application!")
            })
        }

            server.requestHandler({ router.accept(it) }).listen(ConfigManager.webConfig.port) { result ->
                if (result.succeeded()) {
                    logger.info(
                            "==========================================================================================================")
                    logger.info("Cloudopt Next started is success!")
                    logger.info(
                            "==========================================================================================================")

                } else {
                    logger.error(
                            "==========================================================================================================")
                    logger.error("Cloudopt Next started is error! " + result.cause())
                    logger.error(
                            "==========================================================================================================")
                }
            }


    }

    override fun stop() {

    }

}