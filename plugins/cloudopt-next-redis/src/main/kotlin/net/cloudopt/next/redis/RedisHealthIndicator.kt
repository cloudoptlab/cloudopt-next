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

import net.cloudopt.next.core.Worker.await
import net.cloudopt.next.health.HealthChecksResult
import net.cloudopt.next.health.HealthChecksStatusEnum
import net.cloudopt.next.health.HealthIndicator

/**
 * Used to automatically check the redis connection status.
 */
class RedisHealthIndicator : HealthIndicator {
    override suspend fun checkHealth(): HealthChecksResult {

        val result = HealthChecksResult(data = mutableMapOf())

        return await {
            RedisManager.configMap.forEach { map ->
                val connectionResult = HealthChecksResult(data = mutableMapOf())
                connectionResult.data["cluster"] = map.value.cluster
                if (map.value.cluster) {
                    if (RedisManager.clusterConnectionMap[map.key]?.isOpen?.not() == true) {
                        connectionResult.status = HealthChecksStatusEnum.DOWN
                    }
                } else {
                    if (RedisManager.connectionMap[map.key]?.isOpen?.not() == true) {
                        connectionResult.status = HealthChecksStatusEnum.DOWN
                    }
                }
                if (map.value.publish) {
                    if (map.value.cluster) {
                        connectionResult.data["publish"] =
                            if (RedisManager.clusterPublishConnectionMap[map.key]?.isOpen == true) {
                                HealthChecksStatusEnum.UP
                            } else {
                                HealthChecksStatusEnum.DOWN
                            }
                    } else {
                        connectionResult.data["publish"] =
                            if (RedisManager.publishConnectionMap[map.key]?.isOpen == true) {
                                HealthChecksStatusEnum.UP
                            } else {
                                HealthChecksStatusEnum.DOWN
                            }
                    }
                }
                if (map.value.publish) {
                    if (map.value.cluster) {
                        connectionResult.data["subscribe"] =
                            if (RedisManager.clusterSubscribeConnectionMap[map.key]?.isOpen == true) {
                                HealthChecksStatusEnum.UP
                            } else {
                                HealthChecksStatusEnum.DOWN
                            }
                    } else {
                        connectionResult.data["subscribe"] =
                            if (RedisManager.subscribeConnectionMap[map.key]?.isOpen == true) {
                                HealthChecksStatusEnum.UP
                            } else {
                                HealthChecksStatusEnum.DOWN
                            }
                    }
                }

                result.data[map.key] = connectionResult

            }
            return@await result
        }

    }
}