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
    fun get() {
        renderJson(json("result" to "get"))
    }

    @POST
    fun post() {
        renderJson(json("result" to "post"))
    }

    @PUT
    fun put() {
        renderJson(json("result" to "put"))
    }

    @DELETE
    fun delete() {
        renderJson(json("result" to "delete"))
    }

    @PATCH
    fun patch() {
        renderJson(json("result" to "patch"))
    }

    @GET("/defaultError")
    fun defaultError() {
        fail(500)
    }


    @GET("/customError")
    fun customError() {
        fail(500, RuntimeException("Test Error"))
    }

}