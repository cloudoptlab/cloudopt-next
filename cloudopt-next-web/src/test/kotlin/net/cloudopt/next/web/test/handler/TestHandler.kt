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
package net.cloudopt.next.web.test.handler

import net.cloudopt.next.logging.Logger
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.handler.Handler


/*
 * @author: Cloudopt
 * @Time: 2018/1/26
 * @Description: Test Handler
 */
class TestHandler : Handler() {

    companion object {
        val logger = Logger.getLogger(TestHandler::class.java)
    }

    override fun preHandle(resource: Resource) {
        logger.info(resource.request.absoluteURI() + "-preHandle")
    }

    override fun postHandle(resource: Resource) {
        logger.info(resource.request.absoluteURI() + "-postHandle")
    }

    override fun afterCompletion(resource: Resource) {
        logger.info(resource.request.absoluteURI() + "-afterCompletion")
    }


}