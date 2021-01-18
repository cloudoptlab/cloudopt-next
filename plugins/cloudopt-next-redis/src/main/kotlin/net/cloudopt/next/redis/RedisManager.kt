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

    lateinit var connection: StatefulRedisConnection<String, String>

    lateinit var publishConnection: StatefulRedisPubSubConnection<String, String>

    lateinit var subscribeConnection: StatefulRedisPubSubConnection<String, String>

    lateinit var clusterConnection: StatefulRedisClusterConnection<String, String>

    lateinit var clusterPublishConnection: StatefulRedisClusterPubSubConnection<String, String>

    lateinit var clusterSubscribeConnection: StatefulRedisClusterPubSubConnection<String, String>

    /**
     * Extension for StatefulRedisConnection to create RedisCoroutinesCommands
     */
    @ExperimentalLettuceCoroutinesApi
    fun coroutines(): RedisCoroutinesCommands<String, String> {
        return connection.coroutines()
    }

    /**
     * Extension for StatefulRedisClusterConnection to create RedisClusterCoroutinesCommands.
     */
    @ExperimentalLettuceCoroutinesApi
    fun clusterCoroutines(): RedisClusterCoroutinesCommands<String, String> {
        return clusterConnection.coroutines()
    }

    /**
     * Returns the RedisCommands API for the current connection. Does not create a new connection.
     */
    fun sync(): RedisCommands<String, String> {
        return connection.sync()
    }

    /**
     * Returns the RedisAdvancedClusterCommands API for the current connection. Does not create a new connection.
     */
    fun clusterSync(): RedisAdvancedClusterCommands<String, String> {
        return clusterConnection.sync()
    }

    /**
     * Returns the RedisAsyncCommands API for the current connection. Does not create a new connection.
     */
    fun asyn(): RedisAsyncCommands<String, String> {
        return connection.async()
    }

    /**
     * Returns the RedisAdvancedClusterAsyncCommands API for the current connection. Does not create a new connection.
     */
    fun clusterAsync(): RedisAdvancedClusterAsyncCommands<String, String> {
        return clusterConnection.async()
    }

    /**
     * Extension for StatefulRedisConnection to create RedisCoroutinesCommands
     */
    @ExperimentalLettuceCoroutinesApi
    suspend fun publish(channel: String, message: String): Long? {
        if (cluster) {
            return clusterPublishConnection.coroutines().publish(channel, message)
        }
        return publishConnection.coroutines().publish(channel, message)
    }

    /**
     * Listen for messages published to the given channels.channels
     * @param channels the channels
     */
    fun subscribe(vararg channels: String) {
        if (cluster) {
            return clusterSubscribeConnection.sync().subscribe(*channels)
        }
        return subscribeConnection.sync().subscribe(*channels)
    }

    /**
     * Add a new listener to consume push messages.
     * @param listener the listener, must not be null.
     */
    fun addListener(listener: PushListener) {
        if (cluster) {
            return clusterSubscribeConnection.addListener(listener)
        }
        subscribeConnection.addListener(listener)
    }

}