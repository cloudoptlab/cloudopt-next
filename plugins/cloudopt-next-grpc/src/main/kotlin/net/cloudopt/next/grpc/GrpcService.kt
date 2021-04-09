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
package net.cloudopt.next.grpc

import io.grpc.ServerInterceptor
import kotlin.reflect.KClass

/**
 * You can use @GrpcService to mark a specific class, and the grpc
 * plugin will automatically scan for and register classes that
 * have declared the @GrpcService annotation when it starts.
 * @property interceptors Array<KClass<out ServerInterceptor>>
 * @constructor
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@MustBeDocumented
annotation class GrpcService(val interceptors: Array<KClass<out ServerInterceptor>> = [])
