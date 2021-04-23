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

import io.lettuce.core.RedisClient
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.resource.ClientResources
import io.lettuce.core.resource.DefaultClientResources
import net.cloudopt.next.core.Plugin
import net.cloudopt.next.core.Worker
import net.cloudopt.next.core.ConfigManager


/*
 * Lettuce is a scalable Redis client for building non-blocking Reactive applications.
 * Next will automatically read the configuration file and initialize the lettuce,
 * and will modify the default EventLoop of the lettuce to be the EventLoop of the vertx.
 *
 */
class RedisPlugin : Plugin {

    override fun start(): Boolean {
        val redisConfig = ConfigManager.init("redis")
        val uri: String = if ((redisConfig["uri"] as String).isNotBlank()) {
            redisConfig["uri"] as String
        } else {
            "redis://localhost"
        }

        /**
         * Modify the default EventLoop of the lettuce to be the EventLoop of the vertx.
         */
        val res: ClientResources = DefaultClientResources.builder()
            .eventExecutorGroup(Worker.vertx.nettyEventLoopGroup())
            .build()
        if (redisConfig["cluster"] != null && redisConfig["cluster"] as Boolean) {
            startCluster(res, uri, redisConfig)
        } else {
            startAlone(res, uri, redisConfig)
        }
        return true
    }

    override fun stop(): Boolean {
        if (RedisManager.cluster) {
            RedisManager.clusterClient.shutdown()
        } else {
            RedisManager.client.shutdown()
        }
        return true
    }

    private fun startAlone(res: ClientResources, uri: String, redisConfig: MutableMap<String, Any>) {
        RedisManager.cluster = false
        RedisManager.client = RedisClient.create(res, uri)
        RedisManager.connection = RedisManager.client.connect()
        if (redisConfig["publish"] != null && redisConfig["publish"] as Boolean) {
            RedisManager.publishConnection = RedisManager.client.connectPubSub()
        }
        if (redisConfig["subscribe"] != null && redisConfig["subscribe"] as Boolean) {
            RedisManager.subscribeConnection = RedisManager.client.connectPubSub()
        }
    }

    private fun startCluster(res: ClientResources, uri: String, redisConfig: MutableMap<String, Any>) {
        RedisManager.cluster = true
        RedisManager.clusterClient = RedisClusterClient.create(res, uri)
        RedisManager.clusterConnection = RedisManager.clusterClient.connect()
        if (redisConfig["publish"] != null && redisConfig["publish"] as Boolean) {
            RedisManager.clusterPublishConnection = RedisManager.clusterClient.connectPubSub()
        }
        if (redisConfig["subscribe"] != null && redisConfig["subscribe"] as Boolean) {
            RedisManager.clusterSubscribeConnection = RedisManager.clusterClient.connectPubSub()
        }
    }

}