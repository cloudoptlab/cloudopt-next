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

import net.cloudopt.next.web.NextServer
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.annotation.AutoHandler

@AutoHandler
class CorsHandler : Handler {
    override suspend fun preHandle(resource: Resource): Boolean {
        if (NextServer.webConfig.cors) {
            if (resource.request.getHeader("Origin").isNullOrBlank()) {
                resource.setHeader("Access-Control-Allow-Origin", "*")
            } else {
                resource.setHeader("Access-Control-Allow-Origin", resource.request.getHeader("Origin"))
            }
            resource.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS")
            resource.setHeader("Access-Control-Allow-Headers", "Content-Type")
            resource.setHeader("Access-Control-Max-Age", "1800")
            resource.setHeader("Sec-Fetch-Mode", "cors")

        }
        return true
    }

    override suspend fun postHandle(resource: Resource): Boolean {
        return true
    }

    override suspend fun afterRender(resource: Resource, bodyString: String): Boolean {
        return true
    }

    override suspend fun afterCompletion(resource: Resource): Boolean {
        return true
    }
}
