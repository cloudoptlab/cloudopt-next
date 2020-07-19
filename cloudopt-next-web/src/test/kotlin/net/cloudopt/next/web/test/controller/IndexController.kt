/*
 * Copyright 2017-2020 original authors
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
package net.cloudopt.next.web.test.controller

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import net.cloudopt.next.validator.annotation.Chinese
import net.cloudopt.next.web.NextServer.logger
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.Worker
import net.cloudopt.next.web.event.AfterEvent
import net.cloudopt.next.web.event.EventManager
import net.cloudopt.next.web.render.View
import net.cloudopt.next.web.route.*
import net.cloudopt.next.web.test.Student
import net.cloudopt.next.web.test.interceptor.TestInterceptor1
import javax.validation.constraints.Min

/*
 * @author: Cloudopt
 * @Time: 2018/1/26
 * @Description: Test Controller
 */
@API(value = "/", interceptor = [TestInterceptor1::class])
class IndexController : Resource() {

    @GET
    fun index() {
        setCookie("test", "cookie", "127.0.0.1", 360000, "/", false, false)
        renderHtml(view = "index")
    }

    @GET("delete")
    fun delete() {
        delCookie("test")
        renderHtml(view = "index")
    }

    @GET("args")
    fun argsController(
        @Chinese(false)
        @Parameter("name", defaultValue = "Peter")
        name: String,
        @Min(18)
        @Parameter("age")
        age: Int
    ) {
        var map = hashMapOf<String, Any>()
        map["name"] = name
        map["age"] = age
        renderJson(map)
    }

    @POST("body")
    fun bodyController(@RequestBody body: Student) {
        renderJson(body)
    }

    @POST
    fun postIndex() {
        renderText("POST")
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

    @GET("event")
    fun event() {
        EventManager.send("net.cloudopt.web.test", "This is test message!")
        renderJson("Send Event!")
    }

    @GET("500")
    fun fail500() {
        fail(500)
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
        files.forEach { file ->
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

    @GET("socket")
    fun socketView() {
        renderHtml(view = "socket")
    }

    @GET("afterEvent")
    @AfterEvent(["net.cloudopt.web.test"])
    fun afterEvent() {
        renderText("AfterEvent is success!")
    }

}