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

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import net.cloudopt.next.json.JsonProvider
import net.cloudopt.next.json.Jsoner
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.utils.Classer
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.web.handler.AutoHandler
import net.cloudopt.next.web.handler.ErrorHandler
import net.cloudopt.next.web.handler.Handler
import net.cloudopt.next.web.render.Render
import net.cloudopt.next.web.render.RenderFactory
import net.cloudopt.next.web.route.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions

/*
 * @author: Cloudopt
 * @Time: 2018/1/17
 * @Description: Cloudopt Next Server
 */
object NextServer {

    @JvmStatic
    open var verticleID = "net.cloudopt.next.web"

    val logger = Logger.getLogger(NextServer.javaClass)

    @JvmStatic
    open val resources: MutableList<KClass<Resource>> = arrayListOf()

    @JvmStatic
    open val sockJSes: MutableList<KClass<SockJSResource>> = arrayListOf()

    @JvmStatic
    open val webSockets: MutableList<KClass<WebSocketResource>> = arrayListOf()

    @JvmStatic
    open val handlers = arrayListOf<Handler>()

    @JvmStatic
    open val plugins = arrayListOf<Plugin>()

    @JvmStatic
    open val interceptors = mutableMapOf<String, MutableList<KClass<out Interceptor>>>()

    @JvmStatic
    open val validators = mutableMapOf<String, MutableMap<HttpMethod, Array<KClass<out Validator>>>>()

    @JvmStatic
    open val resourceTables = arrayListOf<ResourceTable>()

    @JvmStatic
    open var packageName = ""

    @JvmStatic
    open var errorHandler: KClass<ErrorHandler> =
        Classer.loadClass(ConfigManager.config.errorHandler) as KClass<ErrorHandler>

    init {
        /**
         * Set json provider
         */
        Jsoner.jsonProvider = Classer.loadClass(ConfigManager.config.jsonProvider).createInstance() as JsonProvider
    }

    /**
     * Scan by annotation and register as a route.
     */
    private fun scan() {
        ConfigManager.config.vertxDeployment.workerPoolName = verticleID

        //Set log color
        Logger.configuration.color = ConfigManager.config.logColor

        //Scan cloudopt handler
        Classer.scanPackageByAnnotation("net.cloudopt.next", true, AutoHandler::class)
            .forEach { clazz ->
                handlers.add(clazz.createInstance() as Handler)
            }

        packageName = if (ConfigManager.config.packageName.isNotBlank()) {
            ConfigManager.config.packageName
        } else {
            throw RuntimeException("Package name must not be null!")
        }

        //Scan custom handler
        Classer.scanPackageByAnnotation(packageName, true, AutoHandler::class)
            .forEach { clazz ->
                handlers.add(clazz.createInstance() as Handler)
            }

        //Scan sockJS
        Classer.scanPackageByAnnotation(packageName, true, SocketJS::class)
            .forEach { clazz ->
                sockJSes.add(clazz as KClass<SockJSResource>)
            }

        //Scan webSocket
        Classer.scanPackageByAnnotation(packageName, true, WebSocket::class)
            .forEach { clazz ->
                webSockets.add(clazz as KClass<WebSocketResource>)
            }

        //Scan resources
        Classer.scanPackageByAnnotation(packageName, true, API::class)
            .forEach { clazz ->
                resources.add(clazz as KClass<Resource>)
            }

        for (clazz in resources) {

            // Get api annotation
            val annotation: API? = clazz.findAnnotation<API>()

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
            var functions = clazz.functions

            functions.forEach { function ->

                var functionsAnnotations = function.annotations

                var resourceUrl = ""

                var httpMethod: HttpMethod = HttpMethod.GET

                var valids: Array<KClass<out Validator>> = arrayOf()

                var blocking = false

                functionsAnnotations.forEach { functionAnnotation ->
                    when (functionAnnotation) {
                        is GET -> {
                            resourceUrl = "${annotation?.value}${functionAnnotation.value}"
                            httpMethod = HttpMethod(functionAnnotation.method)
                            valids = functionAnnotation.valid
                        }
                        is POST -> {
                            resourceUrl = "${annotation?.value}${functionAnnotation.value}"
                            httpMethod = HttpMethod(functionAnnotation.method)
                            valids = functionAnnotation.valid
                        }
                        is PUT -> {
                            resourceUrl = "${annotation?.value}${functionAnnotation.value}"
                            httpMethod = HttpMethod(functionAnnotation.method)
                            valids = functionAnnotation.valid
                        }
                        is DELETE -> {
                            resourceUrl = "${annotation?.value}${functionAnnotation.value}"
                            httpMethod = HttpMethod(functionAnnotation.method)
                            valids = functionAnnotation.valid
                        }
                        is PATCH -> {
                            resourceUrl = "${annotation?.value}${functionAnnotation.value}"
                            httpMethod = HttpMethod(functionAnnotation.method)
                            valids = functionAnnotation.valid
                        }
                        is net.cloudopt.next.web.route.HttpMethod -> {
                            resourceUrl = "${annotation?.value}${functionAnnotation.value}"
                            httpMethod = HttpMethod(functionAnnotation.method)
                            valids = functionAnnotation.valid
                        }
                        is Blocking -> {
                            blocking = true
                        }
                    }

                    if (resourceUrl.isNotBlank()) {
                        var temp = mutableMapOf<HttpMethod, Array<KClass<out Validator>>>()
                        temp.put(httpMethod, valids)
                        if (validators.containsKey(resourceUrl)) {
                            validators.get(resourceUrl)?.putAll(temp)
                        } else {
                            validators.put(resourceUrl, temp)
                        }
                    }

                }

                if (resourceUrl.isNotBlank()) {
                    var resourceTable = ResourceTable(
                        resourceUrl,
                        httpMethod,
                        clazz,
                        function.name,
                        blocking,
                        function,
                        function.typeParameters
                    )
                    resourceTables.add(resourceTable)
                }
            }


        }
    }

    /**
     * Get package path via class.
     * @param clazz Class<*>
     */
    @JvmStatic
    fun run(clazz: Class<*>) {
        ConfigManager.config.packageName = clazz.`package`.name
        run()
    }

    /**
     * Get package path via class.
     * @param clazz Class<*>
     */
    @JvmStatic
    fun run(clazz: KClass<*>) {
        ConfigManager.config.packageName = clazz.java.`package`.name
        run()
    }

    /**
     * Get package path by package's name.
     * @param pageName package's name
     */
    @JvmStatic
    fun run(pageName: String) {
        ConfigManager.config.packageName = pageName
        run()
    }

    /**
     * Get package path by package's name.
     */
    @JvmStatic
    fun run() {
        scan()
        /**
         * Print banner
         */
        Banner.print()
        startPlugins()
        Worker.deploy("net.cloudopt.next.web.NextServerVerticle")
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                NextServer.stop()
            }
        })
    }

    /**
     * Add custom render.
     * @see net.cloudopt.next.web.render.Render
     * @param extension render's name
     * @param render Render object
     * @return CloudoptServer
     */
    @JvmStatic
    fun addRender(extension: String, render: Render): NextServer {
        RenderFactory.add(extension, render)
        return this
    }

    /**
     * Set the default render.
     * @param name render name
     * @return CloudoptServer
     */
    @JvmStatic
    fun setDefaultRender(name: String): NextServer {
        RenderFactory.setDefaultRender(name)
        return this
    }

    /**
     * Add the plugins that need to be started and the plugins will start first after the server starts.
     * @see net.cloudopt.next.web.Plugin
     * @param plugin Plugin object
     * @return CloudoptServer
     */
    @JvmStatic
    fun addPlugin(plugin: Plugin): NextServer {
        plugins.add(plugin)
        return this
    }

    /**
     * Add a handler that needs to be started and the handler will handle all requests.
     * @see net.cloudopt.next.web.handler.Handler
     * @param handler Handler
     * @return CloudoptServer
     */
    @JvmStatic
    fun addHandler(handler: Handler): NextServer {
        handlers.add(handler)
        return this
    }

    /**
     * Register all plugins
     */
    @JvmStatic
    fun startPlugins() {
        NextServer.plugins.forEach { plugin ->
            if (plugin.start()) {
                NextServer.logger.info("[PLUGIN] Registered plugin：" + plugin.javaClass.name)
            } else {
                NextServer.logger.info("[PLUGIN] Started plugin was error：" + plugin.javaClass.name)
            }
        }
    }

    /**
     * Stop all plugins
     */
    @JvmStatic
    fun stopPlugins() {
        NextServer.plugins.forEach { plugin ->
            if (!plugin.stop()) {
                NextServer.logger.info("[PLUGIN] Stoped plugin was error：${plugin.javaClass.name}")
            }
        }
    }

    /**
     * Stop the the Vertx instance and release any resources held by it.
     * <p>
     * The instance cannot be used after it has been closed.
     * <p>
     * The actual close is asynchronous and may not complete until after the call has returned.
     */
    @JvmStatic
    fun stop() {
        stopPlugins()
        Worker.undeploy("net.cloudopt.next.web.CloudoptServerVerticle")
        Worker.close()
        NextServer.logger.info("Next has exited.")
    }


}