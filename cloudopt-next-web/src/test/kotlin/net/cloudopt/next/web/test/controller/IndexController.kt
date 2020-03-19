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
package net.cloudopt.next.web.test.controller

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import net.cloudopt.next.web.CloudoptServer.logger
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.Worker
import net.cloudopt.next.web.event.EventManager
import net.cloudopt.next.web.render.View
import net.cloudopt.next.web.route.API
import net.cloudopt.next.web.route.Blocking
import net.cloudopt.next.web.route.GET
import net.cloudopt.next.web.route.POST


/*
 * @author: Cloudopt
 * @Time: 2018/1/26
 * @Description: Test Controller
 */
@API("/")
class IndexController : Resource() {

    @GET
    fun index() {
        redirect("/html")
    }

    @POST
    fun postIndex() {
        renderText("POST")
    }

    @GET("html")
    fun html() {
        println(getLang())
        setCookie("test", "cookie", "127.0.0.1", 360000, "/", false, false)
        renderHtml(view = "index")
    }

    @GET("free")
    fun free() {
        var view = View()
        view.view = "index"
        view.parameters.put("name", "free")
        renderFree(view)
    }

    @GET("hbs")
    fun hbs() {
        var view = View()
        view.view = "index"
        view.parameters.put("name", "hbs")
        renderHbs(view)
    }

    @GET("json")
    fun json() {
        var map = hashMapOf<String, Any>()
        map.put("a", 1)
        map.put("b", 2)
        renderJson(map)
    }

    @GET("beetl")
    fun beetl() {
        var view = View()
        view.view = "index"
        view.parameters.put("name", "beetl")
        renderBeetl(view)
    }

    @GET("event")
    fun event() {
        EventManager.send("net.cloudopt.web.test", "This is test message!")
        renderJson("Send Event!")
    }

    @GET("500")
    fun fail500() {
        fail500()
    }

    @GET("i18n")
    fun i18n() {
        renderText(getLang())
    }

    @GET("asyn")
    fun asyn() {
        Worker.worker<Any>(Handler<Promise<Any>> {
            println("This is worker")
        }, Handler<AsyncResult<Any>> {

        })
        renderText("success!")
    }

    @POST("file")
    fun file() {
        var files = getFiles()
        files?.forEach { file ->
            println("-------------------------------------")
            println("FileName: ${file.fileName()}")
            println("UploadedFileName: ${file.uploadedFileName()}")
            println("ContentType: ${file.contentType()}")
            println("-------------------------------------")
        }

        renderText("success!")
    }


    @GET("cookie/:index")
    fun cookie() {
        try {
            val sleep = Math.max(1L, (Math.random() * 3).toLong())
            this.setCookie("hello", getParam("index") ?: "-1")
            context.vertx().setTimer(sleep * 1000L) {
                renderText(getCookie("hello") ?: "Can't find the cookie!")
            }
        } catch (e: Exception) {
            logger.error("error {}", e)
        }

    }

    @Blocking
    @GET("blocking")
    fun blocking() {
        renderText("This is Blocking!")
    }


}