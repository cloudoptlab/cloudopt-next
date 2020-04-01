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

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.utils.Beaner
import net.cloudopt.next.utils.Classer
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.web.handler.AutoHandler
import net.cloudopt.next.web.handler.ErrorHandler
import net.cloudopt.next.web.handler.Handler
import net.cloudopt.next.web.render.Render
import net.cloudopt.next.web.render.RenderFactory
import net.cloudopt.next.web.route.*
import kotlin.reflect.KClass

/*
 * @author: Cloudopt
 * @Time: 2018/1/17
 * @Description: Cloudopt Next Server
 */
object CloudoptServer {

    @JvmStatic
    open var verticleID = "net.cloudopt.next.web"

    val logger = Logger.getLogger(CloudoptServer.javaClass)

    @JvmStatic
    open val resources: MutableList<Class<Resource>> = arrayListOf()

    @JvmStatic
    open val handlers = arrayListOf<Handler>()

    @JvmStatic
    open val plugins = arrayListOf<Plugin>()

    @JvmStatic
    open val interceptors = mutableMapOf<String, MutableList<KClass<out Interceptor>>>()

    @JvmStatic
    open val validators = mutableMapOf<String, MutableMap<HttpMethod, Array<KClass<out Validator>>>>()

    @JvmStatic
    open val controllers = arrayListOf<ResourceTable>()

    @JvmStatic
    lateinit open var vertx: Vertx

    @JvmStatic
    open var packageName = ""

    @JvmStatic
    open var errorHandler = Beaner.newInstance<ErrorHandler>(Classer.loadClass(ConfigManager.config.errorHandler))

    fun scan() {
        ConfigManager.config.vertxDeployment.workerPoolName = verticleID

        //Set log color
        Logger.configuration.color = ConfigManager.config.logColor

        //Scan cloudopt handler
        Classer.scanPackageByAnnotation("net.cloudopt.next", true, AutoHandler::class.java)
            .forEach { clazz ->
                handlers.add(Beaner.newInstance(clazz))
            }

        packageName = if (ConfigManager.config.packageName.isNotBlank()) {
            ConfigManager.config.packageName
        } else {
            throw RuntimeException("Package name must not be null!")
        }

        //Scan custom handler
        Classer.scanPackageByAnnotation(packageName, true, AutoHandler::class.java)
            .forEach { clazz ->
                handlers.add(Beaner.newInstance(clazz))
            }

        //Scan resources
        Classer.scanPackageByAnnotation(packageName, true, API::class.java)
            .forEach { clazz ->
                resources.add(clazz as Class<Resource>)
            }

        resources.forEach { clazz ->

            // Get api annotation
            var annotation: API? = clazz.getDeclaredAnnotation(API::class.java)

            //Register interceptor
            annotation?.interceptor?.forEach { inClass ->
                var url = annotation.value
                if (url.endsWith("/")) {
                    url = url + "*"
                } else {
                    url = url + "/*"
                }
                if (interceptors.containsKey(url)) {
                    interceptors.get(url)!!.add(inClass)
                } else {
                    interceptors.put(url, mutableListOf(inClass))
                }

            }

            //Get methods annotation
            var methods = clazz.methods

            methods.forEach { method ->

                var methodAnnotations = method.annotations

                var resourceUrl = ""

                var httpMethod: HttpMethod = HttpMethod.GET

                var valids: Array<KClass<out Validator>> = arrayOf()

                var blocking = false

                methodAnnotations.forEach { methodAnnotation ->

                    when (methodAnnotation) {
                        is GET -> {
                            resourceUrl = "${annotation?.value}${methodAnnotation.value}"
                            httpMethod = methodAnnotation.httpMethod
                            valids = methodAnnotation.valid
                        }
                        is POST -> {
                            resourceUrl = "${annotation?.value}${methodAnnotation.value}"
                            httpMethod = methodAnnotation.httpMethod
                            valids = methodAnnotation.valid
                        }
                        is PUT -> {
                            resourceUrl = "${annotation?.value}${methodAnnotation.value}"
                            httpMethod = methodAnnotation.httpMethod
                            valids = methodAnnotation.valid
                        }
                        is DELETE -> {
                            resourceUrl = "${annotation?.value}${methodAnnotation.value}"
                            httpMethod = methodAnnotation.httpMethod
                            valids = methodAnnotation.valid
                        }
                        is PATCH -> {
                            resourceUrl = "${annotation?.value}${methodAnnotation.value}"
                            httpMethod = methodAnnotation.httpMethod
                            valids = methodAnnotation.valid
                        }
                        is Blocking -> {
                            blocking = true
                        }
                    }

                    if (resourceUrl.isNotBlank()) {
                        var temp = mutableMapOf<HttpMethod, Array<KClass<out Validator>>>()
                        temp.put(httpMethod, valids)
                        validators.put(resourceUrl, temp)
                    }

                }

                if (resourceUrl.isNotBlank()) {
                    var resourceTable = ResourceTable(resourceUrl, httpMethod, clazz, method.name, blocking, method)
                    controllers.add(resourceTable)
                }
            }


        }
    }

    @JvmStatic
    fun run(clazz: Class<*>) {
        ConfigManager.config.packageName = clazz.`package`.name
        run()
    }

    @JvmStatic
    fun run(clazz: KClass<*>) {
        ConfigManager.config.packageName = clazz.java.`package`.name
        run()
    }

    @JvmStatic
    fun run(pageName: String) {
        ConfigManager.config.packageName = pageName
        run()
    }

    @JvmStatic
    fun run() {
        vertx = Vertx.vertx(ConfigManager.config.vertx)
        Worker.deploy("net.cloudopt.next.web.CloudoptServerVerticle")
    }

    @JvmStatic
    fun addRender(extension: String, render: Render): CloudoptServer {
        RenderFactory.add(extension, render)
        return this
    }

    @JvmStatic
    fun setDefaultRender(name: String): CloudoptServer {
        RenderFactory.setDefaultRender(name)
        return this
    }

    @JvmStatic
    fun addPlugin(plugin: Plugin): CloudoptServer {
        plugins.add(plugin)
        return this
    }

    @JvmStatic
    fun addHandler(handler: Handler): CloudoptServer {
        handlers.add(handler)
        return this
    }

    @JvmStatic
    fun stop() {
        vertx.undeploy("net.cloudopt.next.web.CloudoptServerVerticle")
        vertx.close()
    }


}