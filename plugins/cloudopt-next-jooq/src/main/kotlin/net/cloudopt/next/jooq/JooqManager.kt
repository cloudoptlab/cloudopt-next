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

import org.jooq.DSLContext

object JooqManager {

    init {
        System.getProperties().setProperty("org.jooq.no-logo", "true")
    }

    @JvmStatic
    var dslMap: MutableMap<String, DSLContext> = mutableMapOf()

    val dsl:DSLContext
        get() = if (dslMap.contains("default")) {
            dslMap["default"]!!
        } else {
            throw RuntimeException("There is no default data source, or there is no data source named 'default'!")
        }

}