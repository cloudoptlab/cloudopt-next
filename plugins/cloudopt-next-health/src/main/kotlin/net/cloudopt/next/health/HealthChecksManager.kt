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
package net.cloudopt.next.health

import net.cloudopt.next.core.Worker
import net.cloudopt.next.core.Worker.global

object HealthChecksManager {

    var config = HealthChecksConfig()

    var timerId: Long = -1

    @JvmStatic
    private val healthIndicatorList = mutableMapOf<String, HealthIndicator>()

    @JvmStatic
    private val healthChecksHook = mutableMapOf<String, HealthChecksHook>()

    @JvmStatic
    private val healthChecksReport = mutableMapOf<String, Any>(
        "applicationName" to config.applicationName,
        "status" to HealthChecksStatusEnum.UP
    )

    /**
     * Registers a health check procedure.
     * @param name String the name of the procedure, must not be {@code null} or empty
     * @param healthIndicatorInter HealthIndicator the procedure, must not be {@code null}
     */
    fun register(name: String, healthIndicatorInter: HealthIndicator) {
        healthIndicatorList[name] = healthIndicatorInter
    }

    /**
     * Unregisters a procedure.
     * @param name String the name of the procedure
     */
    fun unregister(name: String) {
        healthIndicatorList.remove(name)
    }

    /**
     * Registers a health check hook.
     * @param name String the name of the hook, must not be {@code null} or empty
     * @param hook HealthChecksHook the hook, must not be {@code null}
     */
    fun registerHook(name: String, hook: HealthChecksHook) {
        healthChecksHook[name] = hook
    }

    /**
     * Unregisters a hook.
     * @param name String the name of the hook
     */
    fun unRegisterHook(name: String) {
        healthChecksHook.remove(name)
    }


    /**
     * Create a timer that will check all registered health checkers against the time specified in the configuration
     * file.
     */
    fun creatTimer() {
        Worker.setTimer(config.intervalTime, true) { id ->
            timerId = id
            global{
                checkAllThings()
            }
        }
    }

    /**
     * Stop the timer.
     */
    fun stopTimer() {
        Worker.cancelTimer(timerId)
    }

    /**
     * Perform all health checks, and perform all hooks after the checks.
     */
    private suspend fun checkAllThings() {
        healthIndicatorList.forEach { (name, healthIndicator) ->
            try {
                val checksResult = healthIndicator.checkHealth()
                healthChecksReport[name] = checksResult
            } catch (e: Exception) {
                healthChecksReport[name] =
                    HealthChecksResult(status = HealthChecksStatusEnum.DOWN, data = mutableMapOf())
            }
        }
        healthChecksHook.values.forEach { hook ->
            hook.hook(healthChecksReport)
        }
    }

    /**
     * Obtain a report of your health checks.
     * @return MutableMap<String, Any>
     */
    fun report(): MutableMap<String, Any> {
        return healthChecksReport
    }

}