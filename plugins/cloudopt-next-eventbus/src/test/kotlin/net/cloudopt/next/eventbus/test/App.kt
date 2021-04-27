package net.cloudopt.next.eventbus.test

import net.cloudopt.next.eventbus.EventBusPlugin
import net.cloudopt.next.web.NextServer

fun main() {
    NextServer.addPlugin(EventBusPlugin())
    NextServer.run()
}