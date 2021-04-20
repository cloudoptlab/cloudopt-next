package net.cloudopt.next.redis

import net.cloudopt.next.web.NextServer
import net.cloudopt.next.web.health.HealthChecksManager
import net.cloudopt.next.web.health.HealthChecksPlugin

class TestCase {
}

fun main() {
    NextServer.addPlugin(RedisPlugin())
    HealthChecksManager.register("redis", RedisHealthIndicator())
    NextServer.addPlugin(HealthChecksPlugin())
    NextServer.run()
}