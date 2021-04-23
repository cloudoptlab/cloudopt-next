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
package net.cloudopt.next.web.health.indicators

import net.cloudopt.next.core.Worker.await
import net.cloudopt.next.web.health.HealthChecksResult
import net.cloudopt.next.web.health.HealthIndicator
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean


/**
 * Get jvm related information。
 */
class JvmHealthIndicator :HealthIndicator{
    override suspend fun checkHealth(): HealthChecksResult {
        return await{
            val runtimeMXBean: RuntimeMXBean = ManagementFactory.getRuntimeMXBean()
            val memoryMxBean = ManagementFactory.getMemoryMXBean()
            return@await HealthChecksResult(data = mutableMapOf(
                "name" to runtimeMXBean.specName,
                "version" to runtimeMXBean.specVersion,
                "vendor" to runtimeMXBean.specVersion,
                "vmVendor" to runtimeMXBean.vmVendor,
                "vmVersion" to runtimeMXBean.vmVersion,
                "heapMemoryUsageMax" to  memoryMxBean.heapMemoryUsage.max,
                "heapMemoryUsageInit" to  memoryMxBean.heapMemoryUsage.init,
                "heapMemoryUsageCommitted" to  memoryMxBean.heapMemoryUsage.committed,
                "heapMemoryUsageUsed" to  memoryMxBean.heapMemoryUsage.used,
                "nonHeapMemoryUsageMax" to  memoryMxBean.nonHeapMemoryUsage.max,
                "nonHeapMemoryUsageInit" to  memoryMxBean.nonHeapMemoryUsage.init,
                "nonHeapMemoryUsageCommitted" to  memoryMxBean.nonHeapMemoryUsage.committed,
                "nonHeapMemoryUsageUsed" to  memoryMxBean.nonHeapMemoryUsage.used
            ))
        }
    }
}