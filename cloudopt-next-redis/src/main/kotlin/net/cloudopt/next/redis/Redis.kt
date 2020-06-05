/*
 * Copyright 2017 Cloudopt.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */


package net.cloudopt.next.redis

import io.vertx.redis.RedisClient
import java.util.concurrent.ConcurrentHashMap

/**
 * @author: Cloudopt
 * @Time: 2018/2/8
 * @Description: Redis.
 * Redis tools class
 */
object Redis {

    internal var mainCache: Cache? = null

    @JvmStatic
    lateinit var asyn: RedisClient

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
            throw IllegalArgumentException("cacheName can not be blank")
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
        val notThreadLocalJedis = jedis == null
        if (notThreadLocalJedis) {
            jedis = cache.jedisPool.resource
            cache.setThreadLocalJedis(jedis)
        }
        try {
            return callback.call(cache)
        } finally {
            if (notThreadLocalJedis) {
                cache.removeThreadLocalJedis()
                jedis.close()
            }
        }
    }
}




