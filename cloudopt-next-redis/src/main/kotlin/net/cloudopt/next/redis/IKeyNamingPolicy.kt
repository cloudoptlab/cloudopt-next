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


package net.cloudopt.next.redis

/**
 * @author: Cloudopt
 * @Time: 2018/2/8
 * @Description: IKeyNamingPolicy.
 * Architects can implement this type of global Key naming strategy，
 * For example, Integer, String, OtherType these different types of objects
 * Choose a different naming method, the default naming method is Object.toString ()
 */
interface IKeyNamingPolicy {

    fun getKeyName(key: Any): String

    companion object {

        val defaultKeyNamingPolicy: IKeyNamingPolicy = object : IKeyNamingPolicy {
            override fun getKeyName(key: Any): String {
                return key.toString()
            }
        }
    }
}




