package net.cloudopt.next.web.test

import io.vertx.core.Vertx
import net.cloudopt.next.core.Worker
import net.cloudopt.next.web.NextServer

fun main() {
    Worker.vertxOptions.preferNativeTransport = true
    Worker.vertx = Vertx.vertx(Worker.vertxOptions)
    NextServer.addPlugin(TestPlugin())
    NextServer.run()
}