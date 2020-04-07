/*
 * Copyright 2017 Cloudopt.
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
package net.cloudopt.next.web.handler

import net.cloudopt.next.waf.Filter
import net.cloudopt.next.waf.MongoInjection
import net.cloudopt.next.waf.SQLInjection
import net.cloudopt.next.waf.XSSInjection
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.config.ConfigManager

/*
 * @author: Cloudopt
 * @Time: 2018/1/26
 * @Description: Used to block common network attacks
 */

@AutoHandler
class WafHandler : Handler() {

    companion object {
        @JvmStatic
        private val filters: MutableList<Filter> = mutableListOf()
    }

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

    override fun preHandle(resource: Resource) {
        //Processing request parameters
        resource.request.params().forEach { entry ->
            var value = entry.value
            filters.forEach { filter ->
                if (value.isNotBlank()) {
                    value = filter.filter(value)
                }
            }
            entry.setValue(value)
        }


        //Processing header parameters
        resource.request.headers().forEach { entry ->
            var value = entry.value
            filters.forEach { filter ->
                if (value.isNotBlank()) {
                    value = filter.filter(value)
                }
            }
            entry.setValue(value)
        }


        //Processing cookie parameters
        resource.context.cookies().forEach { entry ->
            var value = entry.value
            filters.forEach { filter ->
                if (value.isNotBlank()) {
                    value = filter.filter(value)
                }
            }
            entry.setValue(value)
        }

        if (ConfigManager.config.waf.plus) {
            resource.setHeader("Cache-Control", "no-store, no-cache")
            resource.setHeader("X-Content-Type-Options", "nosniff")
            resource.setHeader("X-Download-Options", "noopen")
            resource.setHeader("X-XSS-Protection", "1; mode=block")
            resource.setHeader("X-FRAME-OPTIONS", "DENY")
        }

    }

    override fun postHandle(resource: Resource) {
    }

    override fun afterCompletion(resource: Resource) {
    }

}
