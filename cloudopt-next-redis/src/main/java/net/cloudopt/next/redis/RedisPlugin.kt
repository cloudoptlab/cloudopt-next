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

import net.cloudopt.next.redis.serializer.FstSerializer
import net.cloudopt.next.redis.serializer.ISerializer
import net.cloudopt.next.web.Plugin
import net.cloudopt.next.web.config.ConfigManager
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

/**
 * @author: Cloudopt
 * @Time: 2018/2/8
 * @Description: RedisPlugin.
 * RedisPlugin supports multiple Redis servers，
 * just create multiple RedisPlugin objects.
 * corresponding to these multiple different Redis server can be.
 * Multiple RedisPlugin objects can correspond to different Databases in the same Redis service.
 */
class RedisPlugin() : Plugin {

    private var map = ConfigManager.initMap("redis")
    protected var cacheName: String = "default"
    protected var host: String = "localhost"
    protected var port: Int? = null
    protected var timeout: Int? = null
    protected var password: String? = null
    protected var database: Int? = null
    protected var clientName: String? = null

    open var serializer: ISerializer = FstSerializer()
    open var keyNamingPolicy = IKeyNamingPolicy.defaultKeyNamingPolicy

    init {
        if (map.get("name") != null) {
            cacheName = map.get("name") as String
        }

        if (map.get("host") != null) {
            host = map.get("host") as String
        }

        if (map.get("port") != null) {
            port = map.get("port").toString().toInt()
        }

        if (map.get("timeout") != null) {
            timeout = map.get("timeout").toString().toInt()
        }

        if (map.get("password") != null) {
            password = map.get("password") as String
        }

        if (map.get("database") != null) {
            database = map.get("database").toString().toInt()
        }


        if (map.get("clientName") != null) {
            clientName = map.get("clientName") as String
        }

    }

    /**
     * When the setting properties provided by RedisPlugin still can not meet the demand,
     * this method can obtain the JedisPoolConfig object,
     * through which the Redis can be more carefully configured.
     */
    var jedisPoolConfig = JedisPoolConfig()
        protected set

    override fun start(): Boolean {
        val jedisPool: JedisPool
        if (port != null && timeout != null && !password.isNullOrBlank() && database != null && !clientName.isNullOrBlank())
            jedisPool = JedisPool(jedisPoolConfig, host, port!!, timeout!!, password, database!!, clientName)
        else if (port != null && timeout != null && !password.isNullOrBlank() && database != null)
            jedisPool = JedisPool(jedisPoolConfig, host, port!!, timeout!!, password, database!!)
        else if (port != null && timeout != null && !password.isNullOrBlank())
            jedisPool = JedisPool(jedisPoolConfig, host, port!!, timeout!!, password)
        else if (port != null && timeout != null)
            jedisPool = JedisPool(jedisPoolConfig, host, port!!, timeout!!)
        else if (port != null)
            jedisPool = JedisPool(jedisPoolConfig, host, port!!)
        else
            jedisPool = JedisPool(jedisPoolConfig, host)

        val cache = Cache(cacheName, jedisPool, serializer!!, keyNamingPolicy!!)
        Redis.addCache(cache)
        return true
    }

    override fun stop(): Boolean {
        val cache = Redis.removeCache(cacheName)
        try{
            if (cache == Redis.mainCache)
                Redis.setMainCache(null!!)
            cache.jedisPool.destroy()
        }catch (e:KotlinNullPointerException){

        }
        return true
    }


    fun setTestWhileIdle(testWhileIdle: Boolean) {
        jedisPoolConfig.testWhileIdle = testWhileIdle
    }

    fun setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis: Int) {
        jedisPoolConfig.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis.toLong()
    }

    fun setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis: Int) {
        jedisPoolConfig.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis.toLong()
    }

    fun setNumTestsPerEvictionRun(numTestsPerEvictionRun: Int) {
        jedisPoolConfig.numTestsPerEvictionRun = numTestsPerEvictionRun
    }
}


