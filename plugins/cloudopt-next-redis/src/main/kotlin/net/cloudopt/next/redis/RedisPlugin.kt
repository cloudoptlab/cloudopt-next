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
import net.cloudopt.next.core.ConfigManager
import net.cloudopt.next.core.Plugin
import net.cloudopt.next.core.Worker
import net.cloudopt.next.json.Jsoner.jsonToObjectList
import net.cloudopt.next.json.Jsoner.toJsonString


/*
 * Lettuce is a scalable Redis client for building non-blocking Reactive applications.
 * Next will automatically read the configuration file and initialize the lettuce,
 * and will modify the default EventLoop of the lettuce to be the EventLoop of the vertx.
 *
 */
class RedisPlugin : Plugin {

    override fun start(): Boolean {

        if (ConfigManager.configMap.contains("redis")) {
            val redisConfigList: MutableList<RedisConfig> =
                ConfigManager.configMap["redis"]!!.toJsonString().jsonToObjectList(RedisConfig::class)
            redisConfigList.forEach { redisConfig ->
                val res: ClientResources = DefaultClientResources.builder()
                    .eventExecutorGroup(Worker.vertx.nettyEventLoopGroup())
                    .build()
                if (redisConfig.cluster) {
                    startCluster(res, redisConfig)
                } else {
                    startAlone(res, redisConfig)
                }
                RedisManager.configMap[redisConfig.name] = redisConfig
            }
        } else {
            throw RuntimeException("No redis related configuration was found in the configuration to initialize!")
        }



        return true
    }

    override fun stop(): Boolean {
        RedisManager.connectionMap.forEach { map ->
            map.value.close()
        }
        RedisManager.publishConnectionMap.forEach { map ->
            map.value.close()
        }
        RedisManager.subscribeConnectionMap.forEach { map ->
            map.value.close()
        }
        RedisManager.clusterConnectionMap.forEach { map ->
            map.value.close()
        }
        RedisManager.clusterPublishConnectionMap.forEach { map ->
            map.value.close()
        }
        RedisManager.clusterSubscribeConnectionMap.forEach { map ->
            map.value.close()
        }
        return true
    }

    private fun startAlone(res: ClientResources, redisConfig: RedisConfig) {
        val client = RedisClient.create(res, redisConfig.uri)
        RedisManager.connectionMap[redisConfig.name] = client.connect()
        if (redisConfig.publish) {
            RedisManager.publishConnectionMap[redisConfig.name] = client.connectPubSub()
        }
        if (redisConfig.subscribe) {
            RedisManager.subscribeConnectionMap[redisConfig.name] = client.connectPubSub()
        }
    }

    private fun startCluster(res: ClientResources, redisConfig: RedisConfig) {
        val client = RedisClusterClient.create(res, redisConfig.uri)
        RedisManager.clusterConnectionMap[redisConfig.name] = client.connect()
        if (redisConfig.publish) {
            RedisManager.clusterPublishConnectionMap[redisConfig.name] = client.connectPubSub()
        }
        if (redisConfig.subscribe) {
            RedisManager.clusterSubscribeConnectionMap[redisConfig.name] = client.connectPubSub()
        }
    }

}