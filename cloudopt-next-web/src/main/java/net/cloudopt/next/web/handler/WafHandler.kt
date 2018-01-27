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

import io.vertx.ext.web.Cookie
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.waf.Filter
import net.cloudopt.next.waf.MongoInjection
import net.cloudopt.next.waf.SQLInjection
import net.cloudopt.next.waf.XSSInjection
import net.cloudopt.next.web.CloudoptServer
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.web.json.JsonProvider
import net.cloudopt.next.web.json.Jsoner

/*
 * @author: Cloudopt
 * @Time: 2018/1/26
 * @Description: Used to block common network attacks
 */

@AutoHandler
class WafHandler : Handler() {

    companion object {

        @JvmStatic private var filters: MutableList<Filter> = mutableListOf()

    }

    init {
        if (ConfigManager.wafConfig.xss) {
            filters.add(XSSInjection())
        }
        if (ConfigManager.wafConfig.sql) {
            filters.add(SQLInjection())
        }
        if (ConfigManager.wafConfig.mongodb) {
            filters.add(MongoInjection())
        }
    }

    override fun handle() {

        //Processing request parameters
        request.params().forEach { entry ->
            var value = entry.value
            filters.forEach { filter ->
                if (value.isNotBlank()) {
                    value = filter.filter(value)
                }
            }
            entry.setValue(value)
        }

        //Processing header parameters
        request.headers().forEach { entry ->
            var value = entry.value
            filters.forEach { filter ->
                if (value.isNotBlank()) {
                    value = filter.filter(value)
                }
            }
            entry.setValue(value)
        }


        //Processing cookie parameters
        context?.cookies()?.forEach { entry ->
            var value = entry.value
            filters.forEach { filter ->
                if (value.isNotBlank()) {
                    value = filter.filter(value)
                }
            }
            entry.setValue(value)
        }

        if (ConfigManager.wafConfig.plus){
            setHeader("Cache-Control", "no-store, no-cache")
            setHeader("X-Content-Type-Options", "nosniff")
            setHeader("X-Download-Options", "noopen")
            setHeader("X-XSS-Protection", "1; mode=block")
            setHeader("X-FRAME-OPTIONS", "DENY")
        }

    }


}
