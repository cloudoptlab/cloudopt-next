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
package net.cloudopt.next.cache.annotation

import net.cloudopt.next.cache.CacheableAfterHandler
import net.cloudopt.next.cache.CacheableBeforeHandler
import net.cloudopt.next.cache.DefaultKeyGenerator
import net.cloudopt.next.cache.KeyGenerator
import net.cloudopt.next.web.annotation.After
import net.cloudopt.next.web.annotation.Before
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
@Before(invokeBy = [CacheableBeforeHandler::class])
@After(invokeBy = [CacheableAfterHandler::class])
annotation class Cacheable(
    val region: String,
    val key: String = "",
    val keyGenerator: KClass<out KeyGenerator> = DefaultKeyGenerator::class,
    val l2: Boolean = true
)
