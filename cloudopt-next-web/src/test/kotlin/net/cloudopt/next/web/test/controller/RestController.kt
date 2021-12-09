package net.cloudopt.next.web.test.controller

import net.cloudopt.next.json.Jsoner.json
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.annotation.*
import net.cloudopt.next.web.test.handler.TestAfterPrint2Annotation
import net.cloudopt.next.web.test.handler.TestAfterPrintAnnotation
import net.cloudopt.next.web.test.handler.TestBeforePrint2Annotation
import net.cloudopt.next.web.test.handler.TestBeforePrintAnnotation

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

    @GET("/defaultError")
    suspend fun defaultError() {
        fail(402)
    }


    @GET("/customError")
    suspend fun customError() {
        fail(500, RuntimeException("Test Error"))
    }

}