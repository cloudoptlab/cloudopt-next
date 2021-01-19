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
package net.cloudopt.next.web.test.interceptor

import net.cloudopt.next.logging.Logger
import net.cloudopt.next.web.Interceptor
import net.cloudopt.next.web.Resource


/*
 * @author: Cloudopt
 * @Time: 2018/2/28
 * @Description: Test Case
 */
class TestInterceptor1 : Interceptor {
    val logger = Logger.getLogger(this::class.java.simpleName)
    override suspend fun intercept(resource: Resource): Boolean {
        logger.info("TestInterceptor1")
        return true
    }

    override suspend fun response(resource: Resource): Resource {
        return resource
    }


}