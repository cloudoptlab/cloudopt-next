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
package net.cloudopt.next.web

import io.vertx.core.http.HttpMethod
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KTypeParameter

data class ResourceTable(
    var url: String = "", var httpMethod: HttpMethod = HttpMethod.GET,
    var clazz: KClass<Resource> = Resource::class, var methodName: String = "",
    var blocking: Boolean = false,
    var clazzMethod: KFunction<*>,
    var parameterTypes: List<KTypeParameter> = listOf()
)