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
package net.cloudopt.next.utils

import net.cloudopt.next.json.Jsoner.jsonToObject
import net.cloudopt.next.json.Jsoner.toJsonObject
import net.cloudopt.next.json.Jsoner.toJsonString
import kotlin.reflect.KClass

object Maper {

    /**
     * It will convert map to object
     * @param clazz The type after the conversion
     * @return The object after the conversion is completed
     */
    fun MutableMap<String, Any>.toObject(clazz: KClass<*>): Any {
        return this.toJsonString().jsonToObject(clazz)
    }

    /**
     * It will convert object to map
     * @return The map after the conversion is completed
     */
    fun Any.toMap(): MutableMap<String, Any> {
        return this.toJsonString().toJsonObject().map
    }

}