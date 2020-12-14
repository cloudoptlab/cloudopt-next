package net.cloudopt.next.redis

import org.junit.After
import org.junit.Before
import org.junit.Test


/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Test Case
 */
class TestCase {

    @Before
    fun init() {
        RedisPlugin().start()
        Redis.use()?.set("key", "value")
    }

    @After
    fun stop() {
        RedisPlugin().stop()
    }

    @Test
    fun get() {
        println(Redis.use()?.get<String>("key"))
    }

}