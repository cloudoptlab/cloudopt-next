/*
 * Copyright 2017-2020 original authors
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


package net.cloudopt.next.redis
import java.util.concurrent.ConcurrentHashMap

/**
 * @author: Cloudopt
 * @Time: 2018/2/8
 * @Description: Redis.
 * Redis tools class
 */
object Redis {

    internal var mainCache: Cache? = null

    private val cacheMap = ConcurrentHashMap<String, Cache>()

    @JvmStatic
    fun addCache(cache: Cache?) {
        if (cache == null)
            throw IllegalArgumentException("cache can not be null")
        if (cacheMap.containsKey(cache.name))
            throw IllegalArgumentException("The cache name already exists")

        cacheMap[cache.name] = cache
        if (mainCache == null)
            mainCache = cache
    }

    @JvmStatic
    fun removeCache(cacheName: String): Cache {
        return cacheMap.remove(cacheName)!!
    }

    /**
     * This method will provide a chance to set main cache mainCache，
     * otherwise, the first initialized Cache will become mainCache.
     */
    @JvmStatic
    fun setMainCache(cacheName: String) {
        var cacheName = cacheName
        if (cacheName.isBlank())
            Redis.mainCache = null
            return
        cacheName = cacheName.trim { it <= ' ' }
        val cache = cacheMap[cacheName] ?: throw IllegalArgumentException("the cache not exists: " + cacheName)
        Redis.mainCache = cache
    }

    @JvmStatic
    fun use(): Cache? {
        return mainCache
    }

    @JvmStatic
    fun use(cacheName: String): Cache {
        return cacheMap[cacheName]!!
    }

    @JvmStatic
    fun call(callback: ICallback): Any {
        return call(callback, use()!!)
    }

    @JvmStatic
    fun call(callback: ICallback, cacheName: String): Any {
        return call(callback, use(cacheName))
    }

    @JvmStatic
    private fun call(callback: ICallback, cache: Cache): Any {
        var jedis = cache.getThreadLocalJedis()
        try {
            return callback.call(cache)
        } finally {
            cache.removeThreadLocalJedis()
            jedis.close()

        }
    }
}




