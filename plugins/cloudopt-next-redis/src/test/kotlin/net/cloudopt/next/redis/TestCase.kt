package net.cloudopt.next.redis

import net.cloudopt.next.health.HealthChecksManager
import net.cloudopt.next.health.HealthChecksPlugin
import net.cloudopt.next.web.NextServer

class TestCase {
}

fun main() {
    NextServer.addPlugin(RedisPlugin())
    HealthChecksManager.register("redis", RedisHealthIndicator())
    NextServer.addPlugin(HealthChecksPlugin())
    NextServer.run()
}