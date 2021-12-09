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

import io.vertx.codegen.annotations.Nullable
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.Cookie
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.FileUpload
import io.vertx.ext.web.RoutingContext
import net.cloudopt.next.core.Worker
import net.cloudopt.next.core.toObject
import net.cloudopt.next.json.Jsoner.jsonToObject
import net.cloudopt.next.json.Jsoner.jsonToObjectList
import net.cloudopt.next.json.Jsoner.toJsonArray
import net.cloudopt.next.json.Jsoner.toJsonString
import net.cloudopt.next.waf.Wafer
import net.cloudopt.next.web.render.RenderFactory
import net.cloudopt.next.web.render.Template
import kotlin.RuntimeException
import kotlin.reflect.KClass

open class Resource {

    lateinit var context: RoutingContext

    /**
     * Vertx does not store the body content in the response by default, so in order to get the body content in the
     * response, Next will store the body into this variable when rendering.
     */
    lateinit var responseBody: String

    val request: HttpServerRequest
        get() {
            require(this::context.isInitialized) {
                "RoutingContext must init first!"
            }
            return context.request()
        }

    val response: HttpServerResponse
        get() {
            require(this::context.isInitialized) {
                "RoutingContext must init first!"
            }
            return context.response()
        }

    /**
     * Initialize the resource object.
     * @see RoutingContext
     * @param context RoutingContext
     * @return Resource
     */
    open fun init(context: RoutingContext): Resource {
        this.context = context
        return this
    }

    /**
     * Returns the value of a header.
     * @param key a String specifying the name of the header
     * @return The single value of the parameter
     */
    fun getHeader(key: String): String? {
        return Wafer.contentFilter(request.getHeader(key))
    }

    /**
     * Put an HTTP header
     * @param key  the header name
     * @param value  the header value.
     * @return a reference to this, so the API can be used fluently
     */
    fun setHeader(key: String, value: String) {
        response.putHeader(key, value)
    }

    /**
     * Adds a new value with the specified name and value in map of
     * form attributes in the request.
     * @param key The name
     * @param value The value being added
     */
    fun setAttr(key: String, value: String) {
        context.request().formAttributes().add(key, value)
    }

    /**
     * Get the data in the specified form. By default, the form parameters are merged
     * with the path parameters and only need to be obtained by getPara().
     * @param key The name
     */
    fun getAttr(key: String): @Nullable String? {
        return Wafer.contentFilter(context.request().formAttributes().get(key))
    }

    /**
     * Get the data in the form and convert to object. By default, the form parameters are merged
     * with the path parameters and only need to be obtained by getPara().
     * @param clazz The name
     */
    fun <T> getAttrs(clazz: KClass<*>): Any {
        val map = context.request().formAttributes()
        map.forEach {
            map[it.key] = Wafer.contentFilter(it.value)
        }
        return (context.request().formAttributes() as MutableMap<String, Any>).toObject(clazz)
    }

    /**
     * Returns the value of a request parameter.
     * @param name a String specifying the name of the parameter
     * @return The single value of the parameter
     */
    fun getParam(name: String): String? {
        return Wafer.contentFilter(request.getParam(name))
    }

    /**
     * Returns request parameters.
     * @return Parameters map
     */
    fun getParams(): MutableMap<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        request.params().forEach { e ->
            map[e.key] = Wafer.contentFilter(e.value)
        }
        return map
    }

    /**
     * Returns request parameters.
     * @return Parameters Object
     */
    fun getParams(clazz: KClass<*>): Any {
        val map = mutableMapOf<String, Any?>()
        request.params().forEach { e ->
            map[e.key] = Wafer.contentFilter(e.value)
        }
        return map.toJsonString().jsonToObject(clazz)
    }

    /**
     * Get cookie object by cookie name.
     * @param key a String specifying the name of the cookies
     * @return The single value of the cookie
     */
    fun getCookieObj(key: String): Cookie? {
        val cookie = context.getCookie(key)
        cookie.value = Wafer.contentFilter(cookie.value)
        return cookie
    }

    /**
     * Get cookie value by cookie name.
     * @param key a String specifying the name of the cookies
     * @return The single value of the cookie
     */
    fun getCookie(key: String): String? {
        return context.getCookie(key)?.value?.let { Wafer.contentFilter(it) }
    }


    /**
     * Set Cookie
     * @param key cookie name
     * @param value cookie value
     * @param domain website domain
     * @param age -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
     * @param path http path
     * @param httpOnly Only http or https
     */
    @JvmOverloads
    fun setCookie(
        key: String,
        value: String,
        domain: String = "",
        age: Long = 0,
        path: String = "",
        httpOnly: Boolean = false,
        cookieSecureFlag: Boolean = false
    ) {
        val cookie = Cookie.cookie(key, value)
        if (domain.isNotBlank()) {
            cookie.domain = domain
        }
        if (age > 0) {
            cookie.maxAge = age
        }
        if (path.isNotBlank()) {
            cookie.path = path
        }
        cookie.isHttpOnly = httpOnly
        cookie.isSecure = cookieSecureFlag
        setCookie(cookie)
    }

    /**
     * Add a cookie. This will be sent back to the client in the response.
     * @see Cookie
     * @param cookie cookie object
     */
    fun setCookie(cookie: Cookie) {
        context.addCookie(cookie)
    }

    /**
     * Delete Cookie
     * @param key cookie name
     */
    fun delCookie(key: String) {
        context.removeCookie(key)
    }

    /**
     * Get the user's IP, Get the user's IP from "x-forwarded-for" in the header of http first,
     * or "X-Real-IP" if not.
     * @return user's ip
     */
    fun getIp(): String {
        var ip: String = request.getHeader("x-forwarded-for") ?: ""
        ip = if (ip.isBlank()) {
            request.getHeader("X-Real-IP") ?: ""
        } else {
            ip.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        }
        if (ip.isBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("Proxy-Client-IP") ?: ""
        }
        if (ip.isBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("WL-Proxy-Client-IP") ?: ""
        }
        return ip
    }

    /**
     * Using the default render, render the data and write it to response.
     * @param result Any object,
     */
    suspend fun render(result: Any) {
        render("", result)
    }

    /**
     * Using the specified render, render the data and write it to response.
     * @param renderName render name,
     * @param result Any object,
     */
    suspend fun render(renderName: String, result: Any) {
        NextServer.handlers.forEach { handler ->
            if (!handler.postHandle(Resource().init(context))) {
                if (!context.response().ended()) {
                    context.response().end()
                }
                return
            }
        }
        if (renderName.isBlank()) {
            RenderFactory.getDefaultRender().render(this, result)
        } else {
            RenderFactory.get(renderName).render(this, result)
        }
    }

    /**
     * Using the json render, render the Template.parameters and write it to response.
     * @param block
     */
    suspend fun renderJson(block: () -> Template) {
        render(RenderFactory.JSON, block.invoke().parameters)
    }

    /**
     * Using the json render, render the data and write it to response.
     * @param result Any object,
     */
    suspend fun renderJson(result: Any) {
        render(RenderFactory.JSON, result)
    }

    /**
     * Using the text render, render the data and write it to response.
     * @param result Any object,
     */
    suspend fun renderText(result: String) {
        render(RenderFactory.TEXT, result)
    }

    /**
     * Using the html render, render the data and write it to response.
     * @see Template
     * @param block
     */
    suspend fun renderHtml(block: () -> Template) {
        render(RenderFactory.HTML, block.invoke())
    }

    /**
     * Using the hbs render, render the data and write it to response.
     * @see Template
     * @param block
     */
    suspend fun renderHbs(block: () -> Template) {

        render(RenderFactory.HBS, block.invoke())
    }

    /**
     * Using the freemarker render, render the data and write it to response.
     * @see Template
     * @param block
     */
    suspend fun renderFree(block: () -> Template) {
        render(RenderFactory.FREE, block.invoke())
    }

    /**
     * Same as {@link #sendFile(String, long)} using offset @code{0} which means starting from the beginning of the file.
     *
     * @param fileName  path to the file to server
     */
    suspend fun sendFile(fileName: String) {
        response.sendFile(fileName)
    }

    /**
     * Use the 302 status code to redirect to the page.
     * @param url the url of the page you want to redirect to it
     */
    suspend fun redirect(url: String) {
        if (!context.response().ended()) {
            response.statusCode = 302
            response.putHeader("location", url)
            end()
        }
    }

    /**
     * Restarts the current router with a new path and reusing the original method. All path parameters are then parsed
     * and available on the params list. Query params will also be allowed and available.
     *
     * @param url the new http path.
     */
    fun reroute(url: String) {
        context.reroute(url)
    }

    /**
     * Ends the response. If no data has been written to the response body,
     * the actual response won't get written until this method gets called.
     * <p>
     * Once the response has ended, it cannot be used anymore.
     */
    fun end() {
        response.end()
    }

    /**
     * Fail the context with the specified status code.
     * This will cause the router to route the context to any matching failure handlers for the request. If no failure
     * handlers match It will trigger the error handler matching the status code. You can define such error handler with
     * {@link Router#errorHandler(int, Handler)}. If no error handler is not defined, It will send a default failure
     * response with provided status code.
     *
     * @param statusCode Int the HTTP status code of the response
     * @param throwable Throwable the throwable used when signalling failure
     */
    fun fail(statusCode: Int, throwable: Throwable = RuntimeException("Something is wrong, " +
            "but no exception messages are caught.")) {
        context.fail(statusCode, throwable)
    }

    /**
     * Get the language used by the client, if the client does not specify the language, the default is en_US.
     * @return String
     */
    fun getLang(): String {
        return if (context.preferredLanguage().tag().isNullOrEmpty() || context.preferredLanguage().subtag()
                .isNullOrEmpty()
        ) {
            "en_US"
        } else {
            "${context.preferredLanguage().tag()}_${context.preferredLanguage().subtag()}"
        }
    }

    /**
     * @return Get the entire HTTP request body as a {@link Buffer}. The context must have first been routed to a
     * {@link io.vertx.ext.web.handler.BodyHandler} for this to be populated.
     */
    fun getBody(): Buffer? {
        return context.body
    }

    /**
     * @return  the entire HTTP request body as a string, assuming UTF-8 encoding.
     */
    fun getBodyString(): String? {
        return context.bodyAsString
    }

    /**
     * @return  the entire HTTP request body as a json object, assuming UTF-8 encoding.
     */
    fun getBodyJson(): Any {
        return context.bodyAsJson
    }

    /**
     * @return  the entire HTTP request body as a json and convert object, assuming UTF-8 encoding.
     */
    fun getBodyJson(clazz: KClass<*>): Any {
        return context.bodyAsJson.mapTo(clazz.java)
    }

    /**
     * @return  the entire HTTP request body as a json array, assuming UTF-8 encoding.
     */
    fun getBodyJsonArray(): Any {
        return context.bodyAsJson.toString().toJsonArray()
    }

    /**
     * @return  the entire HTTP request body as a json and convert object array, assuming UTF-8 encoding.
     */
    fun <T> getBodyJsonArray(clazz: KClass<*>): MutableList<T> {
        return context.bodyAsJson.toString().jsonToObjectList(clazz)
    }

    /**
     * @return a set of fileuploads (if any) for the request. The context must have first been routed to a
     * {@link io.vertx.ext.web.handler.BodyHandler} for this to work.
     */
    fun getFiles(): MutableSet<FileUpload> {
        return context.fileUploads()
    }

    /**
     * By default, if executeBlocking is called several times from the same context (e.g. the same verticle instance)
     * then the different executeBlocking are executed serially (i.e. one after another).If you don’t care about
     * ordering you can call the function.
     *
     * @param handler handler representing the blocking code to run
     */
    fun blocking(
        handler: Handler<Promise<Any>>
    ) {
        Worker.worker(handler)
    }

    /**
     * Generating view objects by kotlin dsl.
     * @param block [@kotlin.ExtensionFunctionType] Function1<View, Unit>
     * @return View
     */
    fun template(block: Template.() -> Unit): Template {
        val template = Template()
        template.block()
        return template
    }

}


