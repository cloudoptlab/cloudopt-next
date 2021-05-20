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
package net.cloudopt.next.waf

import net.cloudopt.next.core.ConfigManager
import net.cloudopt.next.core.toObject
import net.cloudopt.next.waf.injection.Filter
import net.cloudopt.next.waf.injection.MongoInjection
import net.cloudopt.next.waf.injection.SQLInjection
import net.cloudopt.next.waf.injection.XSSInjection

object Wafer {

    @JvmStatic
    private val filters: MutableList<Filter> = mutableListOf()

    val config: WafConfigBean = ConfigManager.init("waf").toObject(WafConfigBean::class)

    init {
        if (config.xss) {
            filters.add(XSSInjection())
        }
        if (config.sql) {
            filters.add(SQLInjection())
        }
        if (config.mongodb) {
            filters.add(MongoInjection())
        }
    }

    /**
     * Filter dangerous strings in content.
     * @param str String
     * @return safe string
     */
    fun contentFilter(str: String?): String? {
        var value: String? = str
        return if (str.isNullOrBlank()) {
            value
        } else {
            filters.forEach { filter ->
                if (value?.isNotBlank() == true) {
                    value = filter.filter(value ?: "")
                }
            }
            value
        }
    }

}