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
package net.cloudopt.next.utils

/*
 * @author: Cloudopt
 * @Time: 2018/1/4
 * @Description: This is for instantiating objects
 */
object Beaner {
    /**
     * instantiate objects
     * @param clazz class
     * @return objects
     */
    @kotlin.Deprecated("This method will be removed soon, please use kotlin native syntax.",
        ReplaceWith("clazz.createInstance() as T")
    )
    @Throws(InstantiationException::class, IllegalAccessException::class)
    fun <T> newInstance(clazz: Class<*>): T {
        return clazz.newInstance() as T
    }
}
