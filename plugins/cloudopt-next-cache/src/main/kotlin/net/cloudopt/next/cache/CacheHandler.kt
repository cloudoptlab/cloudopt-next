/*
 * Copyright 2017-2020 original authors
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

import io.vertx.core.http.HttpMethod
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.handler.Handler

/**
 * If the route accessed is in the cache list, the first access is automatically cached
 */

class CacheHandler : Handler {

    override fun preHandle(resource: Resource): Boolean {
        val httpMethod = resource.request.method()
        val url = resource.context.normalisedPath()
        val region = getRegion(httpMethod, url)
        val key = getKey(httpMethod, url)
        if (isCacheEnabledUrl(httpMethod, url) && CacheManager.channel.exists(region, key)) {
            val cacheData: CacheData = CacheManager.channel.get(region, key).value as CacheData
            resource.response.headers().addAll(cacheData.headers)
            resource.response.end(cacheData.bodyString)
            return false
        }
        return true
    }

    override fun postHandle(resource: Resource): Boolean {
        return true
    }

    override fun afterRender(resource: Resource, bodyString: String): Boolean {
        val httpMethod = resource.request.method()
        val url = resource.context.normalisedPath()
        if (isCacheEnabledUrl(httpMethod, url)) {
            var cacheData = CacheData(bodyString = bodyString, headers = resource.response.headers())
            CacheManager.channel.set(getRegion(httpMethod, url), getKey(httpMethod, url), cacheData)
        }
        return true
    }

    override fun afterCompletion(resource: Resource): Boolean {
        return true
    }

    private fun isCacheEnabledUrl(method: HttpMethod, url: String): Boolean {
        return CacheManager.cacheEnabledUrl.containsKey(getKey(method, url))
    }

    private fun getRegion(method: HttpMethod, url: String): String {
        return CacheManager.cacheEnabledUrl[getKey(method, url)] ?: ""
    }

    private fun getKey(method: HttpMethod, url: String): String {
        return "${CacheManager.PREFIX}:${method}:${url}"
    }
}