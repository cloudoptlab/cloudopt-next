/*
 * Copyright 2017-2020 Cloudopt.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package net.cloudopt.next.web

import net.cloudopt.next.waf.Filter
import net.cloudopt.next.waf.MongoInjection
import net.cloudopt.next.waf.SQLInjection
import net.cloudopt.next.waf.XSSInjection
import net.cloudopt.next.web.config.ConfigManager

object Wafer {

    @JvmStatic
    private val filters: MutableList<Filter> = mutableListOf()

    init {
        if (ConfigManager.config.waf.xss) {
            filters.add(XSSInjection())
        }
        if (ConfigManager.config.waf.sql) {
            filters.add(SQLInjection())
        }
        if (ConfigManager.config.waf.mongodb) {
            filters.add(MongoInjection())
        }
    }

    /**
     * Filter dangerous strings in content.
     * @param str String
     * @return safe string
     */
    fun contentFilter(str:String?): String? {
        var value = str
        filters.forEach { filter ->
            if (value?.isNotBlank() == true) {
                value = filter.filter(value?:"")
            }
        }
        return value
    }

}