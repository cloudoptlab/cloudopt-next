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
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.web.config.ConfigManager
import io.vertx.core.Verticle
import io.vertx.core.http.HttpMethod
import net.cloudopt.next.aop.Beaner
import net.cloudopt.next.aop.Classer
import net.cloudopt.next.web.annotation.*
import net.cloudopt.next.web.handler.AutoHandler
import net.cloudopt.next.web.handler.Handler
import net.cloudopt.next.web.render.Render
import net.cloudopt.next.web.render.RenderFactory
import net.cloudopt.next.yaml.Yamler
import kotlin.reflect.KClass

/*
 * @author: Cloudopt
 * @Time: 2018/1/17
 * @Description: Cloudopt Next Server
 */

object CloudoptServer {

    open var verticleID = "net.cloudopt.next.web"

    private val logger = Logger.getLogger(CloudoptServer.javaClass)

    @JvmStatic
    open val resources: MutableList<Class<Resource>> = arrayListOf()

    @JvmStatic
    open val handlers = arrayListOf<Handler>()

    @JvmStatic
    open val plugins = arrayListOf<Plugin>()

    @JvmStatic
    open val interceptors = mutableMapOf<String, Interceptor>()

    @JvmStatic
    open val validators = mutableMapOf<String, MutableMap<HttpMethod, Class<Validator>>>()

    @JvmStatic
    open val controllers = arrayListOf<ResourceTable>()

    open var vertxOptions = VertxOptions()

    @JvmStatic
    open var vertx: Vertx = Vertx.vertx()

    var verticle: Verticle = CloudoptServerVerticle()

    open var deploymentOptions = DeploymentOptions()

    init {
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
        deploymentOptions.setWorker(true)
        //scan cloudopt handler
        Classer.scanPackageByAnnotation("net.cloudopt.next", true, AutoHandler::class.java)
                .forEach { clazz ->
                    handlers.add(Beaner.newInstance(clazz))
                }

        var packageName = if (ConfigManager.webConfig.packageName.isNotBlank()) {
            ConfigManager.webConfig.packageName
        } else {
            Yamler.getRootClassPath()
        }
        //scan custom handler
        Classer.scanPackageByAnnotation(packageName, true, AutoHandler::class.java)
                .forEach { clazz ->
                    handlers.add(Beaner.newInstance(clazz))
                }

        //scan resources
        Classer.scanPackageByAnnotation(packageName, true, API::class.java)
                .forEach { clazz ->
                    resources.add(clazz as Class<Resource>)
                }

        CloudoptServer.resources.forEach { clazz ->

            // Get api annotation
            var annotation: API = clazz.getAnnotation(API::class.java)

            //Register interceptor
            annotation.interceptor.forEach { inClass ->
                logger.info("Registered interceptor：" + annotation.value + " | " + clazz.javaClass.name)
                var interceptor = Beaner.newInstance<Interceptor>(inClass::class.java)
                var url = annotation.value
                if (url.endsWith("/")) {
                    url = url + "*"
                } else {
                    url = url + "/*"
                }
                interceptors.put(url, interceptor)
            }

            //Get methods annotation
            var methods = clazz.getDeclaredMethods()

            methods.forEach { method ->

                var methodAnnotations = method.getDeclaredAnnotations()

                var resourceUrl = ""

                var httpMethod: HttpMethod = HttpMethod.GET

                var valids: Array<KClass<Validator>> = arrayOf()

                methodAnnotations.forEach { methodAnnotation ->

                    if (methodAnnotation is GET) {
                        resourceUrl = annotation.value + methodAnnotation.value
                        httpMethod = HttpMethod.GET
                        valids = methodAnnotation.valid
                    }

                    if (methodAnnotation is POST) {
                        resourceUrl = annotation.value + methodAnnotation.value
                        httpMethod = HttpMethod.POST
                        valids = methodAnnotation.valid
                    }

                    if (methodAnnotation is PUT) {
                        resourceUrl = annotation.value + methodAnnotation.value
                        httpMethod = HttpMethod.PUT
                        valids = methodAnnotation.valid
                    }

                    if (methodAnnotation is DELETE) {
                        resourceUrl = annotation.value + methodAnnotation.value
                        httpMethod = HttpMethod.DELETE
                        valids = methodAnnotation.valid
                    }

                    if (methodAnnotation is PATCH) {
                        resourceUrl = annotation.value + methodAnnotation.value
                        httpMethod = HttpMethod.POST
                        valids = methodAnnotation.valid
                    }

                    valids?.forEach { valid ->
                        var temp = mutableMapOf<HttpMethod, Class<Validator>>()
                        temp.put(httpMethod, valid.java)
                        validators.put(resourceUrl, temp)
                    }

                }

                var resourceTable = ResourceTable(resourceUrl, httpMethod, clazz as KClass<Resource>, method.name)

                controllers.add(resourceTable)

            }


        }
    }

    fun run() {
        // init vertx
        vertx = Vertx.vertx(vertxOptions)
        vertx.deployVerticle("net.cloudopt.next.web.CloudoptServerVerticle", deploymentOptions)
    }

    fun stop() {
        vertx.undeploy("net.cloudopt.next.web.CloudoptServerVerticle")
        vertx.close()
    }

    fun addRender(extension: String, render: Render) {
        RenderFactory.add(extension, render)
    }

    fun setDefaultRender(name: String) {
        RenderFactory.setDefaultRender(name)
    }
}