package net.cloudopt.next.eventbus.test

import net.cloudopt.next.eventbus.EventBusPlugin
import net.cloudopt.next.redis.RedisPlugin
import net.cloudopt.next.web.NextServer

fun main() {
    NextServer.addPlugin(RedisPlugin())
//    EventBusManager.addProvider("default", RedisMQProvider())
    NextServer.addPlugin(EventBusPlugin())
    NextServer.run()
}