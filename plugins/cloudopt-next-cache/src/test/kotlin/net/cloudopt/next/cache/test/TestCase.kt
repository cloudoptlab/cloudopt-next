/*
 * Copyright 2017-2021 Cloudopt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cloudopt.next.cache.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.cloudopt.next.cache.CacheManager
import net.cloudopt.next.cache.CachePlugin
import net.cloudopt.next.core.Worker
import net.cloudopt.next.redis.RedisPlugin
import org.junit.After
import org.junit.Before
import org.junit.Test

class TestCase {

    private val redisPlugin = RedisPlugin()

    private val cachePlugin = CachePlugin()

    private val regionName = "testRegion"

    @ExperimentalCoroutinesApi
    @Before
    fun init() {
        redisPlugin.start()
        cachePlugin.start()
        Dispatchers.setMain(Worker.dispatcher())
    }

    @ExperimentalCoroutinesApi
    @After
    fun clear() {
        redisPlugin.stop()
        cachePlugin.stop()
        Dispatchers.resetMain()
    }

    @Test
    fun setAndGet() = runBlocking {
        CacheManager.set(regionName, "testCache", "success")
        val value: String? = CacheManager.get(regionName, "testCache")
        assert(value == "success")
    }

    @Test
    fun setAndGetOnlyL1() = runBlocking {
        CacheManager.set(regionName, "testCache", "success", l2 = false)
        val value: String? = CacheManager.get(regionName, "testCache", l2 = false)
        assert(value == "success")
    }

    @Test
    fun getNull() = runBlocking {
        val value: String? = CacheManager.get(regionName, "testNullCache")
        assert(value == null)
    }

    @Test
    fun delete() = runBlocking {
        CacheManager.set(regionName, "testDeleteCache", "success")
        CacheManager.delete(regionName, "testDeleteCache")
        val value: String? = CacheManager.get(regionName, "testDeleteCache")
        assert(value == null)
    }


}