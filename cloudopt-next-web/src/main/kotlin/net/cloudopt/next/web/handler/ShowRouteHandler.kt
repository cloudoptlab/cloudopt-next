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
package net.cloudopt.next.web.handler

import net.cloudopt.next.json.Jsoner.toJsonString
import net.cloudopt.next.logging.test.Logger
import net.cloudopt.next.web.NextServer
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.annotation.AutoHandler
import java.text.SimpleDateFormat
import java.util.*

@AutoHandler
class ShowRouteHandler : Handler {

    override fun preHandle(resource: Resource): Boolean {
        if (NextServer.webConfig.showRoute) {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            logger.info(
                "Match route ----------------- " + df.format(Date())
                        + " ------------------------------"
            )
            logger.info("Method       : ${resource.request.method()}")
            logger.info("Path         : ${resource.context.normalizedPath()}")
            logger.info("User-Agent   : ${resource.request.getHeader("User-Agent")}")
            val params = resource.request.params()
            params.entries().forEach { entry ->
                if (params.contains(entry.key)) {
                    params.remove(entry.key)
                    params.add(entry.key, entry.value)
                }
            }
            logger.info("Params       : ${(params?.entries() ?: "[]").toJsonString()}")
            logger.info("Cookie       : ${(resource.request.getHeader("Cookie") ?: "").toJsonString()}")
            logger.info(
                "--------------------------------------------------------------------------------"
            )
        }
        return true
    }

    override fun postHandle(resource: Resource): Boolean {
        return true
    }

    override fun afterRender(resource: Resource, bodyString: String): Boolean {
        return true
    }

    override fun afterCompletion(resource: Resource): Boolean {
        return true
    }

    companion object {
        private val logger = Logger.getLogger(ShowRouteHandler::class)
    }
}
