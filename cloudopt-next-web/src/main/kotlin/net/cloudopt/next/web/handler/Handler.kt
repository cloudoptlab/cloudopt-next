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

import net.cloudopt.next.web.Resource

interface Handler {

    /**
     * Call before routing method processor
     * @param resource Resource
     * @see Resource
     * @return Boolean, if it is true, it will continue to proceed. If it is false,
     * it will close the request directly
     */
    fun preHandle(resource: Resource): Boolean

    /**
     * After entering the routing method, it is called before rendering
     * @param resource Resource
     * @see Resource
     * @return Boolean, if it is true, it will continue to proceed. If it is false,
     * it will close the request directly
     */
    fun postHandle(resource: Resource): Boolean

    /**
     * After rendering
     * @param resource Resource
     * @see Resource
     * @return Boolean, if it is true, it will continue to proceed. If it is false,
     * it will close the request directly
     */
    fun afterRender(resource: Resource, bodyString: String): Boolean

    /**
     * After the request is called, because Next is an asyn web framework,
     * it may not get the context object
     * @param resource Resource
     * @see Resource
     * @return Boolean, Whether it returns true or false, the execution is complete
     */
    fun afterCompletion(resource: Resource): Boolean

}


