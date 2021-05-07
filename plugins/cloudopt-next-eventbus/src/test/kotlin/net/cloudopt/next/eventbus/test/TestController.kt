package net.cloudopt.next.eventbus.test

import net.cloudopt.next.eventbus.AfterEvent
import net.cloudopt.next.eventbus.EventBusManager
import net.cloudopt.next.json.Jsoner
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.annotation.API
import net.cloudopt.next.web.annotation.GET
import net.cloudopt.next.web.annotation.POST

@API("/eventbus")
class TestController : Resource() {

    private val address = "net.cloudopt.next.eventbus.test.a"

    private val message = Jsoner.json("key" to "value")

    @GET
    fun index() {
        renderText("welcome")
    }

    @POST("/send")
    suspend fun send() {
        EventBusManager.send(address, message)
        renderText("success")
    }

    @POST("/publish")
    suspend fun publish() {
        EventBusManager.publish(address, message)
        renderText("success")
    }

    @AfterEvent(["net.cloudopt.next.eventbus.test.a"])
    @POST("/after")
    fun after() {
        context.data()["name"] = "next"
        renderText("success")
    }

}