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

/**
 * For configuring health checks.
 * @property applicationName String Name of the application
 * @property intervalTime Long Interval of each health check
 * @property password String If not blank, access to the Health Check Report api must be with a password
 * @property accessPath String You can customize the access path to health check reports
 * @constructor
 */
data class HealthChecksConfig(
    val applicationName: String = "",
    val intervalTime: Long = 5000,
    val password: String = "",
    val accessPath: String = "/health"
)
