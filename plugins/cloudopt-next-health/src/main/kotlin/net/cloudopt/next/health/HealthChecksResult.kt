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
 * For standardizing inspection results.
 * @property status HealthChecksStatusEnum
 * @see HealthChecksStatusEnum
 * @property data MutableMap<String, Any> You can store any data here
 * @constructor
 */
data class HealthChecksResult(
    var status: HealthChecksStatusEnum = HealthChecksStatusEnum.UP,
    val data: MutableMap<String, Any>
)
