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
package net.cloudopt.next.cache

import io.lettuce.core.codec.ByteArrayCodec
import net.cloudopt.next.cache.serializer.Serializer
import net.cloudopt.next.core.Classer
import net.cloudopt.next.core.Plugin
import net.cloudopt.next.redis.RedisManager
import kotlin.reflect.full.createInstance


class CachePlugin : Plugin {
    override fun start(): Boolean {

        /**
         * After get configuration of the cache, the corresponding caffeine object is generated
         */

        CacheManager.serializer = Classer.loadClass(CacheManager.config.serializer).createInstance() as Serializer

        val regionsList: MutableList<RegionConfig> = CacheManager.config.regions

        if (regionsList.isEmpty()) {
            regionsList.add(RegionConfig(name = "default"))
        }

        regionsList.forEach { region ->
            CacheManager.creatRegion(region.name, parseTime(region.expire), region.maxSize)
        }

        if (RedisManager.cluster) {
            CacheManager.redisClusterConnect = RedisManager.clusterClient.connect(ByteArrayCodec.INSTANCE)
        } else {
            CacheManager.redisConnect = RedisManager.client.connect(ByteArrayCodec.INSTANCE)
        }

        if (CacheManager.config.cluster) {
            RedisManager.addListener(CacheEventListener())
            RedisManager.subscribe(CacheManager.CHANNELS)
        }

        return true
    }

    override fun stop(): Boolean {
        CacheManager.cacheEnabledUrl.clear()
        return true
    }

    private fun parseTime(expiredString: String): Long {
        val unit: Char = Character.toLowerCase(expiredString[expiredString.length - 1])
        var expire = expiredString.substring(0, expiredString.length - 1).toLong()
        when (unit) {
            's' -> {
            }
            'm' -> expire *= 60
            'h' -> expire *= 3600
            'd' -> expire *= 86400
            else -> throw IllegalArgumentException("Unknown expire unit:$unit")
        }
        return expire
    }
}