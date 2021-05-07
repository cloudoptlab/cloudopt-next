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
package net.cloudopt.next.json

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlin.reflect.KClass

interface JsonProvider {

    /**
     * Output json string.
     * @param obj Json Object
     * @return Json string
     */
    fun toJsonString(obj: Any): String

    /**
     * Output MutableMap
     * @param jsonString Json string
     * @return MutableMap
     */
    fun toJsonMap(jsonString: String): MutableMap<String, Any>

    /**
     * Output json object.
     * @param jsonString Json string
     * @param clazz Java class
     * @return Json object
     */
    fun <T> toObject(jsonString: String, clazz: KClass<*>): T

    /**
     * Output json map list.
     * @param jsonString Json string
     * @return MutableList<MutableMap<String,Any>>
     */
    fun toJsonMapList(jsonString: String): MutableList<MutableMap<String, Any>>

    /**
     * Output obj array.
     * @param jsonString Json string
     * @param clazz Java class
     * @return MutableList<Any>
     */
    fun <T> toObjectList(jsonString: String, clazz: KClass<*>): MutableList<T>

    /**
     * Output any list.
     * @param jsonString Json string
     * @return MutableList<Any>
     */
    fun <T> toList(jsonString: String): MutableList<T>

    /**
     * Decode a given JSON string to JSON Object.
     * @see JsonObject
     * @param jsonString String
     * @return JsonObject
     */
    fun toJsonObject(jsonString: String): JsonObject

    /**
     * Decode a given JSON string to JSON Array.
     * @see JsonArray
     * @param jsonString String
     * @return JsonArray
     */
    fun toJsonArray(jsonString: String): JsonArray

}