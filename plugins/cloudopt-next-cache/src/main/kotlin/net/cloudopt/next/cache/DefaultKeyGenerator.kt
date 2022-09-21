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
package net.cloudopt.next.cache

import net.cloudopt.next.web.Resource

class DefaultKeyGenerator : KeyGenerator {

    companion object {
        private fun render(template: String, params: Map<String, Any?>): String {
            val builder = StringBuilder()

            var begin = false
            var paramBegin = false
            var key: StringBuilder? = null
            for (i in template.indices) {
                val c = template[i]
                if (c == '@') {
                    begin = true
                }
                if (begin && c == '{') {
                    paramBegin = true
                    builder.deleteCharAt(builder.length - 1)
                    key = StringBuilder()
                    continue
                }
                if (paramBegin && c != '}') {
                    if (c == '{') {
                        println("Template formatting error! Location: $i")
                    } else {
                        key?.append(c)
                    }
                    continue
                }
                if (paramBegin && c == '}') {
                    builder.append(params[key.toString()])
                    begin = false
                    paramBegin = false
                    continue
                }
                builder.append(c)
            }
            return "${CacheManager.PREFIX}${builder.toString()}"
        }
    }

    override fun generate(key: String, resource: Resource): String {
        val params = mutableMapOf<String, Any?>()
        params.putAll(resource.getParams())
        params["url"] = resource.request.uri()
        params["absoluteURI"] = resource.request.absoluteURI()
        return if (key.isNotBlank()) {
            render(template = key, params = params)
        } else {
            resource.request.uri()
        }

    }
}