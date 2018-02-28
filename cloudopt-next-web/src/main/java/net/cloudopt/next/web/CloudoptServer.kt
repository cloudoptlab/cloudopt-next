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
import io.vertx.core.dns.AddressResolverOptions
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.*
import net.cloudopt.next.aop.Beaner
import net.cloudopt.next.aop.Classer
import net.cloudopt.next.logging.Colorer
import net.cloudopt.next.web.route.*
import net.cloudopt.next.web.handler.AutoHandler
import net.cloudopt.next.web.handler.Handler
import net.cloudopt.next.web.render.Render
import net.cloudopt.next.web.render.RenderFactory
import net.cloudopt.next.yaml.Yamler
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions

/*
 * @author: Cloudopt
 * @Time: 2018/1/17
 * @Description: Cloudopt Next Server
 */
object CloudoptServer {

    @JvmStatic
    open var verticleID = "net.cloudopt.next.web"

    private val logger = Logger.getLogger(CloudoptServer.javaClass)

    @JvmStatic
    open val resources: MutableList<Class<Resource>> = arrayListOf()

    @JvmStatic
    open val handlers = arrayListOf<Handler>()

    @JvmStatic
    open val plugins = arrayListOf<Plugin>()

    @JvmStatic
    open val interceptors = mutableMapOf<String, KClass<out Interceptor>>()

    @JvmStatic
    open val validators = mutableMapOf<String, MutableMap<HttpMethod, KClass<out Validator>>>()

    @JvmStatic
    open val controllers = arrayListOf<ResourceTable>()

    @JvmStatic
    open val vertxOptions = VertxOptions()

    @JvmStatic
    open var vertx: Vertx = Vertx.vertx()

    @JvmStatic
    open val deploymentOptions = DeploymentOptions()

    @JvmStatic
    open var packageName = ""

    @JvmStatic
    open var errorHandler = Beaner.newInstance<Handler>(Classer.loadClass(ConfigManager.webConfig.errorHandler))

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

        //set dns
        var addressResolver = AddressResolverOptions()
        ConfigManager.vertxConfig.addressResolver.split(",").forEach { address ->
            if (address.isNotBlank()) {
                addressResolver.addServer(address)
            }
        }
        vertxOptions.setAddressResolverOptions(addressResolver)

        //set log color
        Colorer.enable = ConfigManager.webConfig.logColor

        //scan cloudopt handler
        Classer.scanPackageByAnnotation("net.cloudopt.next", true, AutoHandler::class.java)
                .forEach { clazz ->
                    handlers.add(Beaner.newInstance(clazz))
                }

        packageName = if (ConfigManager.webConfig.packageName.isNotBlank()) {
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
                interceptors.put(url, inClass)
            }

            //Get methods annotation
            var methods = clazz.methods

            methods.forEach { method ->

                var methodAnnotations = method.annotations

                var resourceUrl = ""

                var httpMethod: HttpMethod = HttpMethod.GET

                var valids: Array<KClass<out Validator>> = arrayOf()

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

                    if(resourceUrl.isNotBlank()){
                        valids?.forEach { valid ->
                            var temp = mutableMapOf<HttpMethod, KClass<out Validator>>()
                            temp.put(httpMethod, valid)
                            validators.put(resourceUrl, temp)
                        }
                    }


                }

                if(resourceUrl.isNotBlank()){
                    var resourceTable = ResourceTable(resourceUrl, httpMethod, clazz, method.name)
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
        // init vertx
        vertx = Vertx.vertx(vertxOptions)
        start()
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
    private fun start() {

        //Register plugins
        plugins.forEach { plugin ->
            if (plugin.start()) {
                logger.info("[PLUGIN] Registered plugin：" + plugin.javaClass.name)
            } else {
                logger.info("[PLUGIN] Started plugin was error：" + plugin.javaClass.name)
            }
        }

        val server = vertx.createHttpServer()

        val router = Router.router(vertx)

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
        logger.info("[FAILURE HANDLER] Registered failure handler：" + errorHandler::class.java.getName())

        router.route("/*").failureHandler { failureRoutingContext ->
            errorHandler.init(failureRoutingContext)
            errorHandler.handle()
        }

        //Register handlers
        handlers.forEach { handler ->
            logger.info("[HANDLER] Registered handler：" + handler::class.java.getName())
            router.route("/*").handler { context ->
                try {
                    handler.init(context)
                    handler.handle()
                    handler.context?.next()
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
        interceptors.forEach { url, clazz ->
            router.route(url).handler { context ->
                var resource = Resource()
                resource.init(context)
                var interceptor = Beaner.newInstance<Interceptor>(clazz.java)
                if (interceptor.intercept(resource)) {
                    context.next()
                } else {
                    interceptor.response(resource).response?.end()
                }
            }
        }

        //Register validators
        validators.forEach { url, map ->
            map.keys.forEach { key ->
                router.route(key, url).handler { context ->
                    try {
                        var v = Beaner.newInstance<Validator>(map.get(key)?.java!!)
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
        controllers.forEach { resourceTable ->

            var controllerObj = Beaner.newInstance<Resource>(resourceTable.clazz)

            router.route(resourceTable.httpMethod, resourceTable.url).handler({ context ->
                try {
                    controllerObj.init(context)
                    var m = resourceTable.clazz.getDeclaredMethod(resourceTable.methodName)
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

            logger.info("[RESOURCE] Registered Resource :" + resourceTable.methodName + " | "
                    + resourceTable.url)
        }

        if (controllers.size == 0) {
            router.route(HttpMethod.GET, "/*").handler({ context ->
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

    @JvmStatic
    fun stop() {
        vertx.close { result ->
            if (result.succeeded()) {
                plugins.forEach { plugin ->
                    if (!plugin.stop()) {
                        logger.info("[PLUGIN] Stoped plugin was error：" + plugin.javaClass.name)
                    }
                }
            }
        }
    }


}