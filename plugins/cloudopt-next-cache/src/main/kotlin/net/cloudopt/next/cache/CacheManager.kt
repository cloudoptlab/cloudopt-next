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

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection
import io.lettuce.core.codec.ByteArrayCodec
import net.cloudopt.next.cache.serializer.DefaultSerializer
import net.cloudopt.next.cache.serializer.Serializer
import net.cloudopt.next.json.Jsoner.toJsonString
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.redis.RedisManager
import net.cloudopt.next.web.Worker.await
import net.cloudopt.next.web.config.ConfigManager
import java.util.concurrent.TimeUnit

object CacheManager {

    internal const val CHANNELS = "NEXT-CACHE-EVENT"

    internal const val PREFIX = "NEXT-CACHE-ROUTE-"

    @JvmStatic
    internal val cacheEnabledUrl = mutableMapOf<String, String>()

    @JvmStatic
    internal val regions = mutableMapOf<String, Cache<String, Any>>()

    @JvmStatic
    private val expireMap: MutableMap<String, Long> = mutableMapOf()

    private val logger: Logger = Logger.getLogger(CacheManager::class)

    internal var serializer: Serializer = DefaultSerializer()

    @JvmStatic
    internal val config = ConfigManager.initObject("cache", CacheConfig::class) as CacheConfig

    @JvmStatic
    internal lateinit var redisConnect: StatefulRedisConnection<ByteArray, ByteArray>

    @JvmStatic
    internal lateinit var redisClusterConnect: StatefulRedisClusterConnection<ByteArray, ByteArray>

    /**
     * Create cache region by name.
     * @param name Region name
     * @param expire specifies that each entry should be automatically removed from the cache once a fixed duration has
     * elapsed after the entry's creation, or the most recent replacement of its value
     * @param maxSize specifies the maximum number of entries the cache may contain. Note that the cache may evict an
     * entry before this limit is exceeded or temporarily exceed the threshold while evicting. As the cache size grows
     * close to the maximum, the cache evicts entries that are less likely to be used again. For example, the cache may
     * evict an entry because it hasn't been used recently or very often
     */
    fun creatRegion(name: String, expire: Long, maxSize: Long) {
        val region: Cache<String, Any> = Caffeine.newBuilder()
            .expireAfterWrite(expire, TimeUnit.SECONDS)
            .maximumSize(maxSize)
            .build()
        regions[name] = region
        expireMap[name] = expire
    }

    /**
     * Performs any pending maintenance operations needed by the cache.
     * @param name region name
     */
    fun cleanRegion(name: String) {
        regions[name]?.cleanUp()
    }

    /**
     * Delete cache region by name.
     * @param name region name
     */
    fun deleteRegion(name: String) {
        regions.remove(name)
    }

    /**
     * It will first try to get the cache from the L1 cache, and if not, it will automatically find the L2 level cache,
     * and if the cache of the specified key does not exist in the L2 level cache either, it will return null.
     * @param regionName region name
     * @param key the key whose associated value is to be returned
     * @param l2 if L2 is true, it will also get the cache in the L2 cache
     * @return the value of key, or null when key does not exist
     */
    suspend fun get(regionName: String, key: String, l2: Boolean = true): Any? {
        return await { future ->
            if (!regions.containsKey(regionName)) {
                future.complete(null)
            }
            var value = regions[regionName]?.getIfPresent(key)
            if (value == null && l2) {
                value = if (RedisManager.cluster) {
                    RedisManager.clusterClient.connect(ByteArrayCodec.INSTANCE).sync().get(key.toByteArray())
                } else {
                    RedisManager.client.connect(ByteArrayCodec.INSTANCE).sync().get(key.toByteArray())
                }

                if (value != null) {
                    regions[regionName]?.put(key, value)
                    logger.debug("From L2: $key")
                    future.complete(serializer.deserialize(value))
                } else {
                    logger.debug("L2 not found: $key")
                    future.complete(null)
                }
            } else {
                logger.debug("From L1: $key")
            }

            if (value == null) {
                future.complete(null)
            } else {
                future.complete(serializer.deserialize(value as ByteArray))
            }


        }
    }

    /**
     * Set the value of a key. After inserting the L1 cache and it will automatically insert the L2 cache.
     * @param regionName region name
     * @param key the key whose associated value
     * @param value the value
     * @param l2 if L2 is true, it will also add the cache in the L2 cache
     * @return String simple-string-reply in L2, If only the L1 cache was manipulated and not the L2 cache,
     * the empty string is returned.
     */
    suspend fun set(regionName: String, key: String, value: Any, l2: Boolean = true): String? {
        return await { future ->
            if (!regions.containsKey(regionName)) {
                future.complete(null)
            }
            regions[regionName]?.put(key, serializer.serialize(value))
            val replyString = if (l2) {
                if (RedisManager.cluster) {
                    redisClusterConnect.sync()
                        .setex(key.toByteArray(), expireMap[regionName] ?: 500, serializer.serialize(value))
                } else {
                    redisConnect.sync()
                        .setex(key.toByteArray(), expireMap[regionName] ?: 500, serializer.serialize(value))
                }
            } else {
                ""
            }
            future.complete(replyString)
        }
    }

    /**
     * Delete the value of the key. After delete the L1 cache and it will cutomatically delete the L2 cache.
     * @param regionName region name
     * @param key the key whose associated value
     * @param publish if cluster is true and publish is true, it will post a message to a channel and then all services
     * will be automatically deleting the cache.
     * @param l2 if L2 is true, it will also delete the cache in the L2 cache
     * @return Long integer-reply The number of keys that were removed in L2, Returns 0 if only the L1 cache was
     * manipulated and not the L2 cache.
     */
    @JvmOverloads
    suspend fun delete(regionName: String, key: String, publish: Boolean = true, l2: Boolean = true): Long? {
        return await { future ->
            if (!regions.containsKey(regionName)) {
                future.complete(null)
            }
            regions[regionName]?.invalidate(key)
            val row = if (l2) {
                if (RedisManager.cluster) {
                    redisClusterConnect.sync().del(key.toByteArray())
                } else {
                    redisConnect.sync().del(key.toByteArray())
                }
            } else {
                0
            }
            if (config.cluster && publish) {
                RedisManager.publishSync(
                    CHANNELS,
                    CacheEventMessage(regionName = regionName, key = key).toJsonString()
                )
            }
            future.complete(row)
        }
    }


}