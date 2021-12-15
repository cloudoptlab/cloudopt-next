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
package net.cloudopt.next.jdbc

import net.cloudopt.next.core.Worker.await
import net.cloudopt.next.health.HealthChecksResult
import net.cloudopt.next.health.HealthChecksStatusEnum
import net.cloudopt.next.health.HealthIndicator

class JDBCHealthIndicator : HealthIndicator {
    override suspend fun checkHealth(): HealthChecksResult {
        val result = HealthChecksResult(data = mutableMapOf())
        return await {
            for (jdbcConfig in JDBCConnectionManager.jdbcConfigMap.values) {
                val connection = JDBCConnectionManager.connectionMap[jdbcConfig.name]
                val dataSourceHealthChecksResult = HealthChecksResult(data = mutableMapOf())
                if (connection?.isClosed != false || !connection.isValid(2000)) {
                    dataSourceHealthChecksResult.status = HealthChecksStatusEnum.DOWN
                }
                dataSourceHealthChecksResult.data["name"] = jdbcConfig.name
                dataSourceHealthChecksResult.data["database"] = jdbcConfig.database
                dataSourceHealthChecksResult.data["pool"] = jdbcConfig.pool
                dataSourceHealthChecksResult.data["driverClassName"] = jdbcConfig.driverClassName
                result.data[jdbcConfig.name] = dataSourceHealthChecksResult
            }
            return@await result
        }


    }
}
