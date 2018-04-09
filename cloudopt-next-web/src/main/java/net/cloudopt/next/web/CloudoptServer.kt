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

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.dns.AddressResolverOptions
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import net.cloudopt.next.aop.Beaner
import net.cloudopt.next.aop.Classer
import net.cloudopt.next.logging.Colorer
import net.cloudopt.next.logging.Logger
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
    open val vertxOptions = VertxOptions()

    @JvmStatic
    lateinit open var vertx: Vertx

    @JvmStatic
    open var httpServerOptions: HttpServerOptions = HttpServerOptions()

    @JvmStatic
    open val deploymentOptions = DeploymentOptions()

    @JvmStatic
    open var packageName = ""

    @JvmStatic
    open var errorHandler = Beaner.newInstance<ErrorHandler>(Classer.loadClass(ConfigManager.webConfig.errorHandler))

    fun scan() {
        vertxOptions.maxWorkerExecuteTime = ConfigManager.vertxConfig.maxWokerExecuteTime
        vertxOptions.setFileResolverCachingEnabled(ConfigManager.vertxConfig.fileCaching)
        vertxOptions.workerPoolSize = ConfigManager.vertxConfig.workerPoolSize
        vertxOptions.eventLoopPoolSize = ConfigManager.vertxConfig.eventLoopPoolSize
        vertxOptions.maxEventLoopExecuteTime = ConfigManager.vertxConfig.maxEventLoopExecuteTime
        vertxOptions.setInternalBlockingPoolSize(ConfigManager.vertxConfig.internalBlockingPoolSize)
        vertxOptions.setClustered(ConfigManager.vertxConfig.clustered)
        vertxOptions.clusterHost = ConfigManager.vertxConfig.clusterHost
        vertxOptions.clusterPort = ConfigManager.vertxConfig.clusterPort
        vertxOptions.clusterPingInterval = ConfigManager.vertxConfig.clusterPingInterval
        vertxOptions.clusterPingReplyInterval = ConfigManager.vertxConfig.clusterPingReplyInterval
        vertxOptions.blockedThreadCheckInterval = ConfigManager.vertxConfig.blockedThreadCheckInterval
        vertxOptions.setHAEnabled(ConfigManager.vertxConfig.hAEnabled)
        vertxOptions.setHAEnabled(ConfigManager.vertxConfig.hAEnabled)
        vertxOptions.haGroup = ConfigManager.vertxConfig.hAGroup
        vertxOptions.quorumSize = ConfigManager.vertxConfig.quorumSize
        vertxOptions.warningExceptionTime = ConfigManager.vertxConfig.warningExceptionTime
        deploymentOptions.workerPoolName = verticleID
        deploymentOptions.workerPoolSize = ConfigManager.vertxConfig.workerPoolSize
        deploymentOptions.maxWorkerExecuteTime = ConfigManager.vertxConfig.maxWokerExecuteTime

        //Set dns
        var addressResolver = AddressResolverOptions()
        ConfigManager.vertxConfig.addressResolver.split(",").forEach { address ->
            if (address.isNotBlank()) {
                addressResolver.addServer(address)
            }
        }
        vertxOptions.setAddressResolverOptions(addressResolver)

        //Set log color
        Colorer.enable = ConfigManager.webConfig.logColor

        //Scan cloudopt handler
        Classer.scanPackageByAnnotation("net.cloudopt.next", true, AutoHandler::class.java)
                .forEach { clazz ->
                    handlers.add(Beaner.newInstance(clazz))
                }

        packageName = if (ConfigManager.webConfig.packageName.isNotBlank()) {
            ConfigManager.webConfig.packageName
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

                    if (methodAnnotation is GET) {
                        resourceUrl = annotation?.value + methodAnnotation.value
                        httpMethod = HttpMethod.GET
                        valids = methodAnnotation.valid
                    }

                    if (methodAnnotation is POST) {
                        resourceUrl = annotation?.value + methodAnnotation.value
                        httpMethod = HttpMethod.POST
                        valids = methodAnnotation.valid
                    }

                    if (methodAnnotation is PUT) {
                        resourceUrl = annotation?.value + methodAnnotation.value
                        httpMethod = HttpMethod.PUT
                        valids = methodAnnotation.valid
                    }

                    if (methodAnnotation is DELETE) {
                        resourceUrl = annotation?.value + methodAnnotation.value
                        httpMethod = HttpMethod.DELETE
                        valids = methodAnnotation.valid
                    }

                    if (methodAnnotation is PATCH) {
                        resourceUrl = annotation?.value + methodAnnotation.value
                        httpMethod = HttpMethod.POST
                        valids = methodAnnotation.valid
                    }

                    if(methodAnnotation is Blocking){
                        blocking = true
                    }

                    if (resourceUrl.isNotBlank()) {
                        var temp = mutableMapOf<HttpMethod, Array<KClass<out Validator>>>()
                        temp.put(httpMethod, valids)
                        validators.put(resourceUrl, temp)
                    }

                }

                if (resourceUrl.isNotBlank()) {
                    var resourceTable = ResourceTable(resourceUrl, httpMethod, clazz, method.name, blocking)
                    controllers.add(resourceTable)
                }
            }


        }
    }

    @JvmStatic
    fun run(clazz: Class<*>) {
        ConfigManager.webConfig.packageName = clazz.`package`.name
        run()
    }

    @JvmStatic
    fun run(clazz: KClass<*>) {
        ConfigManager.webConfig.packageName = clazz.java.`package`.name
        run()
    }

    @JvmStatic
    fun run(pageName: String) {
        ConfigManager.webConfig.packageName = pageName
        run()
    }

    @JvmStatic
    fun run() {
        scan()
        vertx = Vertx.vertx(vertxOptions)
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