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
package net.cloudopt.next.health.indicators

import net.cloudopt.next.core.Worker.await
import net.cloudopt.next.health.HealthChecksResult
import net.cloudopt.next.health.HealthIndicator
import java.io.File

/**
 * For checking the health status of the hard disk.
 */
class DiskSpaceHealthIndicator : HealthIndicator {
    override suspend fun checkHealth(): HealthChecksResult {
        return await {
            val result = HealthChecksResult(data = mutableMapOf())
            val files = File.listRoots()
            files.indices.forEach { i ->
                result.data[i.toString()] = mutableMapOf(
                    "total" to files[i].totalSpace,
                    "free" to files[i].freeSpace
                )
            }
            return@await result
        }

    }
}