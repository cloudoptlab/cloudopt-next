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
package net.cloudopt.next.cache.test.controller

import net.cloudopt.next.cache.CacheManager
import net.cloudopt.next.cache.annotation.Cacheable
import net.cloudopt.next.json.Jsoner.json
import net.cloudopt.next.redis.RedisManager
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.annotation.*


@API(value = "/")
class IndexController : Resource() {

    companion object {
        @JvmStatic
        val conn = RedisManager.sync()
    }

    @GET
    suspend fun getCache() {
        renderJson(CacheManager.get("testRegion", "testGetAndSet") ?: "")
    }

    @GET("redis")
    fun getRedis() {
        renderJson(conn.get("testGetAndSet") ?: "")
    }

    @GET("redis/:key")
    fun getRedisByKey(
        @Parameter("key")
        key: String
    ) {
        renderJson(conn.get(key) ?: "")
    }

    @POST
    suspend fun setCache() {
        renderText(CacheManager.set("testRegion", "testGetAndSet", "success") ?: "null")
    }

    @DELETE
    suspend fun deleteCache() {
        renderText((CacheManager.delete("testRegion", "testGetAndSet") ?: -1).toString())
    }

    @GET("cacheable/:id")
    @Cacheable("testRegion",key = "@{url}-@{id}")
    suspend fun cacheable(){
        renderJson(json("name" to "cacheable"))
    }

}