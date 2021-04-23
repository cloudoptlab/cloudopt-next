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
package net.cloudopt.next.jooq

import net.cloudopt.next.core.Worker.await
import net.cloudopt.next.core.ConfigManager
import net.cloudopt.next.web.health.HealthChecksResult
import net.cloudopt.next.web.health.HealthChecksStatusEnum
import net.cloudopt.next.web.health.HealthIndicator

/**
 * Used to automatically check the database connection status.
 */
class DBHealthIndicator : HealthIndicator {
    override suspend fun checkHealth(): HealthChecksResult {
        val connection = JooqManager.connection
        return await {
            val result = HealthChecksResult(data = mutableMapOf())
            if (connection.isClosed || !connection.isValid(2000)) {
                result.status = HealthChecksStatusEnum.DOWN
            }
            var map = ConfigManager.init("jooq")
            result.data["database"] = map["database"] ?: ""
            result.data["pool"] = map["pool"] ?: ""
            map = ConfigManager.init("datasource")
            result.data["driverClassName"] = map["driverClassName"] ?: ""
            return@await result
        }
    }
}
