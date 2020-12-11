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
package net.cloudopt.next.cache

import net.cloudopt.next.cache.CacheManager.PREFIX
import net.cloudopt.next.web.NextServer
import net.cloudopt.next.web.Plugin
import net.cloudopt.next.web.config.ConfigManager
import net.oschina.j2cache.CacheChannel
import net.oschina.j2cache.J2CacheBuilder
import net.oschina.j2cache.J2CacheConfig


object CacheManager {

    internal const val PREFIX = "NEXT-CACHE-ROUTE-"

    internal val config = ConfigManager.init("cache")

    @JvmStatic
    internal val cacheEnabledUrl = mutableMapOf<String, String>()

    @JvmStatic
    lateinit var channel: CacheChannel
}


class CachePlugin : Plugin {
    override fun start(): Boolean {
        /**
         * Initializing J2Cache configuration
         */
        val j2config = J2CacheConfig()
        j2config.broadcast = "none"
        if (CacheManager.config["broadcast"] != null && CacheManager.config["broadcast"] as Boolean? == true) {
            j2config.broadcast = "lettuce"
        }
        j2config.l1CacheName = "none"
        if (CacheManager.config["l1"] != null && CacheManager.config["l1"] as Boolean? == true) {
            j2config.l1CacheName = "caffeine"
            val regions = CacheManager.config["regions"] as MutableMap<String, String>
            if (regions.isEmpty()) {
                j2config.l1CacheProperties["region.default"] = "1000, 30m"
            }
            for (key in regions.keys) {
                j2config.l1CacheProperties["region.$key"] = regions[key]
            }

        }
        j2config.l2CacheName = "lettuce"
        j2config.isDefaultCacheNullObject = false
        j2config.serialization = CacheManager.config["serialization"] as String? ?: "fastjson"
        j2config.l2CacheProperties["namespace"] = CacheManager.config["namespace"] ?: ""
        j2config.l2CacheProperties["storage"] = CacheManager.config["storage"] ?: "hash"
        j2config.l2CacheProperties["channel"] = CacheManager.config["channel"] ?: "cloudopt-cache"
        j2config.broadcastProperties["channel"] = "cloudopt-cache-mq"
        j2config.l2CacheProperties["scheme"] = CacheManager.config["scheme"] ?: "redis"
        j2config.l2CacheProperties["hosts"] = CacheManager.config["hosts"] ?: "127.0.0.1:6379"
        j2config.l2CacheProperties["password"] = CacheManager.config["password"] ?: ""
        j2config.l2CacheProperties["database"] = CacheManager.config["database"] ?: 0
        j2config.l2CacheProperties["timeout"] = CacheManager.config["timeout"] ?: 10000
        j2config.l2CacheProperties["clusterTopologyRefresh"] = CacheManager.config["clusterTopologyRefresh"]
            ?: 3000

        val builder = J2CacheBuilder.init(j2config)
        CacheManager.channel = builder.channel


        /**
         * Scanning for routes with annotations
         * @see Cache
         */
        for (r in NextServer.resourceTables) {
            val method = r.clazzMethod
            if (method.getDeclaredAnnotation(Cache::class.java) != null) {
                val annotation: Cache = method.getDeclaredAnnotation(Cache::class.java)
                CacheManager.cacheEnabledUrl["${PREFIX}:${r.httpMethod}:${r.url}"] = annotation.region
            }


        }
        return true
    }

    override fun stop(): Boolean {
        CacheManager.channel.close()
        CacheManager.cacheEnabledUrl.clear()
        return true
    }
}