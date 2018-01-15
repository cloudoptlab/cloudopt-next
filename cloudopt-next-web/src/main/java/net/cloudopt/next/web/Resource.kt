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

import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Cookie
import io.vertx.ext.web.RoutingContext
import net.cloudopt.next.web.render.RenderFactory
import net.cloudopt.next.web.render.View

/*
 * @author: Cloudopt
 * @Time: 2018/1/15
 * @Description: Route resource
 */
open class Resource {

    var context:RoutingContext = null!!

    var request: HttpServerRequest
        get() = context.request()

    var response: HttpServerResponse
        get() = context.response()

    open fun init(context: RoutingContext) {
        this.context = context
    }

    /**
     * Returns the value of a header.
     * @param key a String specifying the name of the header
     * @return The single value of the parameter
     */
    fun getHeader(key: String): String {
        return request.getHeader(key)
    }

    fun setHeader(key: String, value: String) {
        response.putHeader(key,value)
    }

    /**
     * Returns the value of a request parameter.
     * @param name a String specifying the name of the parameter
     * @return The single value of the parameter
     */
    fun <T> getParam(name: String): T? {
        val value = request.getParam(name)
        return if (value.isNotBlank()) {
            value as T
        } else {
            null
        }

    }

    /**
     * Get cookie object by cookie name.
     * @param key a String specifying the name of the cookies
     * @return The single value of the cookie
     */
    fun getCookieObj(key: String): Cookie {
        val cookie = context.getCookie(key)
        return cookie
    }

    /**
     * Get cookie value by cookie name.
     * @param key a String specifying the name of the cookies
     * @return The single value of the cookie
     */
    fun getCookie(key: String): String {
        return context.getCookie(key).value
    }


    fun setCookie(key: String, value: String) {
        val cookie = Cookie.cookie(key,value)
        context.addCookie(cookie)
    }

    fun setCookie(key: String, value: String, domain: String) {
        val cookie = Cookie.cookie(key, value)
        cookie.domain = domain
        context.addCookie(cookie)
    }

    fun setCookie(key: String, value: String, domain: String, age: Int) {
        val cookie = Cookie.cookie(key, value)
        cookie.domain = domain
        cookie.setMaxAge(age.toLong())
        context.addCookie(cookie)
    }

    /**
     * Set Cookie
     * @param name cookie name
     * @param value cookie value
     * @param domain website domain
     * @param age -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
     * @param path http path
     */
    fun setCookie(key: String, value: String, domain: String, age: Int, path: String) {
        val cookie = Cookie.cookie(key, value)
        cookie.domain = domain
        cookie.setMaxAge(age.toLong())
        cookie.path = path
        context.addCookie(cookie)
    }

    fun setCookie(cookie: Cookie) {
        context.addCookie(cookie)
    }

    /**
     * Delete Cookie
     * @param name cookie name
     */
    fun delCookie(key: String) {
        context.removeCookie(key)
    }

    fun render(result: Object) {
        RenderFactory.getDefaultRender().render(context.response(), result)
    }

    fun render(renderName: String, result: Any) {
        RenderFactory.get(renderName).render(context.response(), result)
    }

    fun renderJson(result: Any) {
        render(RenderFactory.JSON, result)
    }

    fun renderText(result: Any) {
        render(RenderFactory.TEXT, result)
    }

    fun renderHbs(view: View) {
        render(RenderFactory.HBS, view)
    }

    fun renderBeetl(view: View) {
        render(RenderFactory.BEETL, view)
    }

    fun renderFree(view: View) {
        render(RenderFactory.FREE, view)
    }

    fun end() {
        response.end()
    }

    fun fail(code: Int) {
        context.fail(code)
    }

}
