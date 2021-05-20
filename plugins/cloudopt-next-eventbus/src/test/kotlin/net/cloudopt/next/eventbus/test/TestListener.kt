package net.cloudopt.next.eventbus.test

import io.vertx.core.json.JsonObject
import net.cloudopt.next.eventbus.AutoEvent
import net.cloudopt.next.eventbus.EventListener
import net.cloudopt.next.json.Jsoner.toJsonString

@AutoEvent("net.cloudopt.next.eventbus.test.a")
class TestListener : EventListener {
    override suspend fun listener(message: JsonObject) {
        println(message.toJsonString())
    }
}