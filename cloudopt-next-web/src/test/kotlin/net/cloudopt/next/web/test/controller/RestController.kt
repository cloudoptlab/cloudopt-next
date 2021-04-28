package net.cloudopt.next.web.test.controller

import net.cloudopt.next.json.Jsoner.json
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.annotation.*

@API("/restful")
class RestController:Resource() {

    @GET
    fun get(){
        renderJson(json("result" to "get"))
    }

    @POST
    fun post(){
        renderJson(json("result" to "post"))
    }

    @PUT
    fun put(){
        renderJson(json("result" to "put"))
    }

    @DELETE
    fun delete(){
        renderJson(json("result" to "delete"))
    }

    @PATCH
    fun patch(){
        renderJson(json("result" to "patch"))
    }

}