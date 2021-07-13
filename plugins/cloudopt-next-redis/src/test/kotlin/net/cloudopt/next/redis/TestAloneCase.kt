package net.cloudopt.next.redis

import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.cloudopt.next.core.ConfigManager
import net.cloudopt.next.core.Worker
import net.cloudopt.next.web.NextServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class TestAloneCase {

    @ExperimentalCoroutinesApi
    @Before
    fun init() {
        NextServer.webConfig.env = "alone"
        RedisPlugin().start()
        Dispatchers.setMain(Worker.dispatcher())
    }

    @ExperimentalCoroutinesApi
    @After
    fun clear() {
        Dispatchers.resetMain()
    }

    @ExperimentalLettuceCoroutinesApi
    @Test
    fun getAndSet() = runBlocking {
        var value = RedisManager.coroutines().getset("testAloneGetSet", "success")
        if (value.isNullOrBlank()) {
            value = RedisManager.coroutines().getset("testAloneGetSet", "success")
        }
        assert(value == "success")
    }

    @ExperimentalLettuceCoroutinesApi
    @Test
    fun testPubSub(): Unit = runBlocking {
        RedisManager.addListener(TestEventListener())
        RedisManager.subscribe("testMQ")
        val id = RedisManager.publish("testMQ", "New　Message") ?: -1
        assert(id > -1)
    }


}