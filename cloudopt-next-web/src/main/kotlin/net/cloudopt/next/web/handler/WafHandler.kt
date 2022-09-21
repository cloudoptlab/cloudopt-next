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

import net.cloudopt.next.waf.Wafer
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.annotation.AutoHandler

@AutoHandler
class WafHandler : Handler {

    override suspend fun preHandle(resource: Resource): Boolean {
        if (Wafer.config.plus) {
            resource.setHeader("X-Content-Type-Options", "nosniff")
            resource.setHeader("X-Download-Options", "noopen")
            resource.setHeader("X-XSS-Protection", "1; mode=block")
            resource.setHeader("X-FRAME-OPTIONS", "DENY")
        }
        return true
    }

    override suspend fun postHandle(resource: Resource): Boolean {
        return true
    }

    override suspend fun afterRender(resource: Resource, text: String): Boolean {
        return true
    }

    override suspend fun afterCompletion(resource: Resource): Boolean {
        return true
    }

}
