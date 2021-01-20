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
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import io.lettuce.core.api.push.PushListener
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands
import io.lettuce.core.cluster.api.coroutines
import io.lettuce.core.cluster.api.coroutines.RedisClusterCoroutinesCommands
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection

/*
 * Manager for managing and storing redis clients.
 */
object RedisManager {

    var cluster = false

    open lateinit var client: RedisClient

    open lateinit var clusterClient: RedisClusterClient

    /**
     * Extension for StatefulRedisConnection to create RedisCoroutinesCommands
     */
    @ExperimentalLettuceCoroutinesApi
    fun coroutines(): RedisCoroutinesCommands<String, String> {
        return client.connect().coroutines()
    }

    /**
     * Extension for StatefulRedisClusterConnection to create RedisClusterCoroutinesCommands.
     */
    @ExperimentalLettuceCoroutinesApi
    fun clusterCoroutines(): RedisClusterCoroutinesCommands<String, String> {
        return clusterClient.connect().coroutines()
    }

    /**
     * Returns the RedisCommands API for the current connection. Does not create a new connection.
     */
    fun sync(): RedisCommands<String, String> {
        return client.connect().sync()
    }

    /**
     * Returns the RedisAdvancedClusterCommands API for the current connection. Does not create a new connection.
     */
    fun clusterSync(): RedisAdvancedClusterCommands<String, String> {
        return clusterClient.connect().sync()
    }

    /**
     * Returns the RedisAsyncCommands API for the current connection. Does not create a new connection.
     */
    fun asyn(): RedisAsyncCommands<String, String> {
        return client.connect().async()
    }

    /**
     * Returns the RedisAdvancedClusterAsyncCommands API for the current connection. Does not create a new connection.
     */
    fun clusterAsync(): RedisAdvancedClusterAsyncCommands<String, String> {
        return clusterClient.connect().async()
    }

    /**
     * Extension for StatefulRedisConnection to create RedisCoroutinesCommands
     */
    @ExperimentalLettuceCoroutinesApi
    suspend fun publish(channel: String, message: String): Long? {
        if (cluster) {
            return clusterClient.connect().coroutines().publish(channel, message)
        }
        return client.connect().coroutines().publish(channel, message)
    }

    /**
     * Listen for messages published to the given channels.channels
     * @param channels the channels
     */
    fun subscribe(vararg channels: String) {
        if (cluster) {
            return clusterClient.connectPubSub().sync().subscribe(*channels)
        }
        return client.connectPubSub().sync().subscribe(*channels)
    }

    /**
     * Add a new listener to consume push messages.
     * @param listener the listener, must not be null.
     */
    fun addListener(listener: PushListener) {
        if (cluster) {
            return clusterClient.connectPubSub().addListener(listener)
        }
        client.connectPubSub().addListener(listener)
    }

}