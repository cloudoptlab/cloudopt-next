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
package net.cloudopt.next.redis

import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisFuture
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands
import io.lettuce.core.cluster.api.coroutines
import io.lettuce.core.cluster.api.coroutines.RedisClusterCoroutinesCommands
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection
import io.lettuce.core.pubsub.RedisPubSubListener
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection

/*
 * Manager for managing and storing redis clients.
 */
object RedisManager {

    var configMap: MutableMap<String, RedisConfig> = mutableMapOf()

    val clientMap: MutableMap<String, RedisClient> = mutableMapOf()

    val clusterClientMap: MutableMap<String, RedisClusterClient> = mutableMapOf()

    val connectionMap: MutableMap<String, StatefulRedisConnection<String, String>> = mutableMapOf()

    val publishConnectionMap: MutableMap<String, StatefulRedisPubSubConnection<String, String>> = mutableMapOf()

    val subscribeConnectionMap: MutableMap<String, StatefulRedisPubSubConnection<String, String>> = mutableMapOf()

    val clusterConnectionMap: MutableMap<String, StatefulRedisClusterConnection<String, String>> = mutableMapOf()

    val clusterPublishConnectionMap: MutableMap<String, StatefulRedisClusterPubSubConnection<String, String>> =
        mutableMapOf()

    val clusterSubscribeConnectionMap: MutableMap<String, StatefulRedisClusterPubSubConnection<String, String>> =
        mutableMapOf()

    /**
     * Extension for StatefulRedisConnection to create RedisCoroutinesCommands
     * @param name Multiple data sources are supported from version 3.1.0.0,
     * please declare the data source used.
     */
    @ExperimentalLettuceCoroutinesApi
    fun coroutines(name: String = "default"): RedisCoroutinesCommands<String, String> {
        return connectionMap[name]!!.coroutines()
    }

    /**
     * Extension for StatefulRedisClusterConnection to create RedisClusterCoroutinesCommands.
     * @param name Multiple data sources are supported from version 3.1.0.0,
     * please declare the data source used.
     */
    @ExperimentalLettuceCoroutinesApi
    fun clusterCoroutines(name: String = "default"): RedisClusterCoroutinesCommands<String, String> {
        return clusterConnectionMap[name]!!.coroutines()
    }

    /**
     * Returns the RedisCommands API for the current connection. Does not create a new connection.
     * @param name Multiple data sources are supported from version 3.1.0.0,
     * please declare the data source used.
     */
    fun sync(name: String = "default"): RedisCommands<String, String>? {
        return connectionMap[name]!!.sync()
    }

    /**
     * Returns the RedisAdvancedClusterCommands API for the current connection.
     * Does not create a new connection.
     * @param name Multiple data sources are supported from version 3.1.0.0,
     * please declare the data source used.
     */
    fun clusterSync(name: String = "default"): RedisAdvancedClusterCommands<String, String> {
        return clusterConnectionMap[name]!!.sync()
    }

    /**
     * Returns the RedisAsyncCommands API for the current connection.
     * Does not create a new connection.
     * @param name Multiple data sources are supported from version 3.1.0.0,
     * please declare the data source used.
     */
    fun asyn(name: String = "default"): RedisAsyncCommands<String, String> {
        return connectionMap[name]!!.async()
    }

    /**
     * Returns the RedisAdvancedClusterAsyncCommands API for the current connection. \
     * Does not create a new connection.
     * @param name Multiple data sources are supported from version 3.1.0.0,
     * please declare the data source used.
     */
    fun clusterAsync(name: String = "default"): RedisAdvancedClusterAsyncCommands<String, String> {
        return clusterConnectionMap[name]!!.async()
    }

    /**
     * Post a message to a channel.
     *
     * @param channel the channel type: key.
     * @param message the message type: value.
     * @param name Multiple data sources are supported from version 3.1.0.0,
     * please declare the data source used.
     * @return Long integer-reply the number of clients that received the message.
     */
    @ExperimentalLettuceCoroutinesApi
    suspend fun publish(name: String = "default", channel: String, message: String): Long? {
        if (configMap[name]?.cluster == true) {
            return clusterPublishConnectionMap[name]!!.coroutines().publish(channel, message)
        }
        return publishConnectionMap[name]!!.coroutines().publish(channel, message)
    }

    /**
     * Post a message to a channel.
     *
     * @param channel the channel type: key.
     * @param message the message type: value.
     * @param name Multiple data sources are supported from version 3.1.0.0,
     * please declare the data source used.
     * @return Long integer-reply the number of clients that received the message.
     */
    fun publishSync(name: String = "default", channel: String, message: String): Long? {
        if (configMap[name]?.cluster == true) {
            return clusterPublishConnectionMap[name]!!.sync().publish(channel, message)
        }
        return publishConnectionMap[name]!!.sync().publish(channel, message)
    }

    /**
     * Post a message to a channel.
     *
     * @param channel the channel type: key.
     * @param message the message type: value.
     * @param name Multiple data sources are supported from version 3.1.0.0,
     * please declare the data source used.
     * @return Long integer-reply the number of clients that received the message.
     */
    fun publishAsync(name: String = "default", channel: String, message: String): RedisFuture<Long>? {
        if (configMap[name]?.cluster == true) {
            return clusterPublishConnectionMap[name]!!.async().publish(channel, message)
        }
        return publishConnectionMap[name]!!.async().publish(channel, message)
    }

    /**
     * Listen for messages published to the given channels.channels
     * @param channels the channels
     * @param name Multiple data sources are supported from version 3.1.0.0,
     * please declare the data source used.
     */
    fun subscribe(name: String = "default", vararg channels: String) {
        if (configMap[name]?.cluster == true) {
            return clusterSubscribeConnectionMap[name]!!.sync().subscribe(*channels)
        }
        return subscribeConnectionMap[name]!!.sync().subscribe(*channels)
    }

    /**
     * Add a new listener to consume push messages.
     * @param listener the listener, must not be null.
     * @param name Multiple data sources are supported from version 3.1.0.0,
     * please declare the data source used.
     */
    fun addListener(name: String = "default", listener: RedisPubSubListener<String, String>) {
        if (configMap[name]?.cluster == true) {
            return clusterSubscribeConnectionMap[name]!!.addListener(listener)
        }
        subscribeConnectionMap[name]!!.addListener(listener)
    }

}