package net.cloudopt.next.web.test.controller

import net.cloudopt.next.json.Jsoner.json
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.annotation.*
import net.cloudopt.next.web.test.handler.TestAfterPrint2Annotation
import net.cloudopt.next.web.test.handler.TestAfterPrintAnnotation
import net.cloudopt.next.web.test.handler.TestBeforePrint2Annotation
import net.cloudopt.next.web.test.handler.TestBeforePrintAnnotation
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@API("/restful")
class RestController : Resource() {

    @TestBeforePrintAnnotation
    @TestBeforePrint2Annotation
    @TestAfterPrintAnnotation
    @TestAfterPrint2Annotation
    @GET
    suspend fun get() {
        renderJson(json("result" to "get"))
    }

    @POST
    suspend fun post() {
        renderJson(json("result" to "post"))
    }

    @PUT
    suspend fun put() {
        renderJson(json("result" to "put"))
    }

    @DELETE
    suspend fun delete() {
        renderJson(json("result" to "delete"))
    }

    @PATCH
    suspend fun patch() {
        renderJson(json("result" to "patch"))
    }

    @POST("/cookie")
    suspend fun addCookie() {
        setCookie("key", "value")
        renderText("success")
    }

    @DELETE("/cookie")
    suspend fun delCookie() {
        delCookie("key")
        renderText("success")
    }

    @GET("/defaultError")
    suspend fun defaultError() {
        fail(402)
    }


    @GET("/customError")
    suspend fun customError() {
        fail(500, RuntimeException("Test Error"))
    }

    @GET("/validParam")
    suspend fun vaildParam(
        @Parameter
        @NotNull
        @Min(18)
        age:Int
    ){
        renderText(age.toString())
    }

}