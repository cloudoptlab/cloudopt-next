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

import net.cloudopt.next.cache.annotation.Cacheable
import net.cloudopt.next.core.Worker
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.RouteHandler
import kotlin.reflect.full.createInstance

class CacheableAfterHandler : RouteHandler {

    override suspend fun handle(annotation: Annotation, resource: Resource): Boolean {
        val cacheable: Cacheable = annotation as Cacheable
        val key: String = Worker.await {
            return@await cacheable.keyGenerator.createInstance().generate(cacheable.key, resource)
        }
        val cacheableBean = CacheableBean(body = resource.responseBody)
        resource.response.headers().entries().forEach { it ->
            cacheableBean.heads[it.key] = it.value
        }
        CacheManager.set(regionName = cacheable.region, key = key, value = cacheableBean, l2 = cacheable.l2)
        return true
    }
}