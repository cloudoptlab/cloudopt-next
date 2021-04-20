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
package net.cloudopt.next.redis.test

import net.cloudopt.next.web.Worker.await
import net.cloudopt.next.web.Worker.global
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.web.health.HealthChecksResult
import net.cloudopt.next.web.health.HealthChecksStatusEnum
import net.cloudopt.next.web.health.HealthIndicator
import java.io.File

/**
 * Used to automatically check the redis connection status.
 */
class RedisHealthIndicator : HealthIndicator {
    override suspend fun checkHealth(): HealthChecksResult {
        return await {
            val result = HealthChecksResult(data = mutableMapOf())
            if (RedisManager.cluster){
                if (!RedisManager.clusterConnection.isOpen){
                    result.status = HealthChecksStatusEnum.DOWN
                }
            }else{
                if (!RedisManager.connection.isOpen){
                    result.status = HealthChecksStatusEnum.DOWN
                }
            }
            val redisConfig = ConfigManager.init("redis")
            result.data["cluster"] = RedisManager.cluster
            if (redisConfig["publish"] != null && redisConfig["publish"] as Boolean) {
                if (RedisManager.cluster){
                    result.data["publish"] = if (RedisManager.clusterPublishConnection.isOpen){
                        HealthChecksStatusEnum.UP
                    }else{
                        HealthChecksStatusEnum.DOWN
                    }
                }else{
                    result.data["publish"] = if (RedisManager.publishConnection.isOpen){
                        HealthChecksStatusEnum.UP
                    }else{
                        HealthChecksStatusEnum.DOWN
                    }
                }
            }
            if (redisConfig["subscribe"] != null && redisConfig["subscribe"] as Boolean) {
                if (RedisManager.cluster){
                    result.data["subscribe"] = if (RedisManager.clusterSubscribeConnection.isOpen){
                        HealthChecksStatusEnum.UP
                    }else{
                        HealthChecksStatusEnum.DOWN
                    }
                }else{
                    result.data["subscribe"] = if (RedisManager.subscribeConnection.isOpen){
                        HealthChecksStatusEnum.UP
                    }else{
                        HealthChecksStatusEnum.DOWN
                    }
                }
            }


            return@await result
        }

    }
}