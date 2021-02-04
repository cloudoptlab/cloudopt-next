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
package net.cloudopt.next.web.test.controller

import io.vertx.kotlin.coroutines.awaitEvent
import net.cloudopt.next.web.NextServer.logger
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.Worker
import net.cloudopt.next.web.Worker.await
import net.cloudopt.next.web.Worker.then
import net.cloudopt.next.web.event.AfterEvent
import net.cloudopt.next.web.event.EventManager
import net.cloudopt.next.web.route.*
import net.cloudopt.next.web.test.Student
import net.cloudopt.next.web.test.interceptor.TestInterceptor1
import net.cloudopt.next.web.test.validator.TestCoroutinesValidator
import net.cloudopt.next.web.test.validator.TestValidator
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

/*
 * @author: Cloudopt
 * @Time: 2018/1/26
 * @Description: Test Controller
 */
@API(value = "/", interceptor = [TestInterceptor1::class])
class IndexController : Resource() {

    @GET(valid = [TestValidator::class, TestCoroutinesValidator::class])
    fun index() {
        setCookie("test", "cookie", "127.0.0.1", 360000, "/", false, false)
        renderHtml {
            template {
                name = "index"
            }
        }
    }

    @GET("delete")
    fun delete() {
        delCookie("test")
        renderHtml {
            template {
                name = "index"
            }
        }
    }

    @GET("args")
    suspend fun argsController(
        @NotBlank
        @Parameter
        name: String,
        @Min(18)
        @Parameter
        age: Int
    ) {
        var map = hashMapOf<String, Any>()
        renderJson {
            template {
                parameters["name"] = name
                parameters["age"] = age
            }
        }
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
        renderFree {
            template {
                name = "index"
                parameters["name"] = "free"
            }
        }
    }

    @GET("hbs")
    fun hbs() {
        renderHbs {
            template {
                name = "index"
                parameters["name"] = "hbs"
            }
        }
    }

    @GET("json")
    fun json() {
        val student = Student(name = "andy", sex = 1)
        renderJson {
            template {
                parameters["result"] = student
            }
        }
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

    @GET("worker")
    fun worker() {
        blocking {
            renderText("success!")
        }
    }

    @GET("awaitWorker")
    suspend fun awaitWorker() {
        var id = -1
        id = await { future ->
            println("in await")
            id = 1
            future.complete(id)
        }
        renderText("success! $id")
    }

    @POST("file")
    fun file() {
        val files = getFiles()
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
            Worker.setTimer(sleep * 1000L) {
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
        renderHtml {
            template {
                name = "socket"
            }
        }
    }

    @GET("coroutines", valid = [TestCoroutinesValidator::class])
    suspend fun coroutines() {
        var timeId = awaitEvent<Long> { handler ->
            Worker.setTimer(1000, false, handler)
        }
        println("Await event end! id=$timeId")
        renderText("Await event end! id=$timeId")
    }

    @GET("afterEvent")
    @AfterEvent(["net.cloudopt.web.test"])
    fun afterEvent() {
        this.context.data().put("key", "value")
        renderText("AfterEvent is success!")
    }

    @GET("then")
    fun thenGet() {
        then {
            println(1)
        }

        then {
            println(2)
        }
        renderText("Then action is success!")
    }

}