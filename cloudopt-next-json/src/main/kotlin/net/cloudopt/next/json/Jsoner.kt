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

object Jsoner {

    @JvmStatic
    var jsonProvider: JsonProvider = DefaultJSONProvider()

    /**
     * Output json string.
     * @return Json string
     */
    fun Any.toJsonString(): String {
        return jsonProvider.toJsonString(this)
    }

    /**
     * Output MutableMap
     * @return MutableMap
     */
    fun String.jsonToMutableMap(): MutableMap<String, Any> {
        return jsonProvider.toJsonMap(this)
    }

    /**
     * Output json object.
     * @param clazz Java class
     * @return Json object
     */
    fun <T> String.jsonToObject(clazz: KClass<*>): T {
        return jsonProvider.toObject(this, clazz)
    }

    /**
     * Output json map list.
     * @return MutableList<MutableMap<String,Any>>
     */
    fun String.jsonToMutableMapList(): MutableList<MutableMap<String, Any>> {
        return jsonProvider.toJsonMapList(this)
    }

    /**
     * Output obj array.
     * @param clazz Java class
     * @return MutableList<Any>
     */
    fun <T> String.jsonToObjectList(clazz: KClass<*>): MutableList<T> {
        return jsonProvider.toObjectList(this, clazz)
    }

    /**
     * Output any list.
     * @return MutableList<Any>
     */
    fun String.jsonToObjectList(): MutableList<Any> {
        return jsonProvider.toList(this)
    }

    /**
     * Decode a given JSON string to JSON Object.
     * @see JsonObject
     * @return JsonObject
     */
    fun String.toJsonObject(): JsonObject {
        return jsonProvider.toJsonObject(this)
    }

    /**
     * Decode a given JSON string to JSON Array.
     * @see JsonArray
     * @return JsonArray
     */
    fun String.toJsonArray(): JsonArray {
        return jsonProvider.toJsonArray(this)
    }

    /**
     * Returns a new read-only map with the specified contents, given as a list of JsonObject
     * where the first value is the key and the second is the value.
     *
     * If multiple pairs have the same key, the resulting map will contain the value from the last of those pairs.
     * @param pairs Array<out Pair<String, Any>>
     * @return JsonObject
     * @see JsonObject
     */
    fun json(vararg pairs: Pair<String, Any>): JsonObject {
        return JsonObject(pairs.toMap())
    }

    /**
     * Returns an JsonArray containing the specified elements.
     * @param elements Array<out Any>
     * @return JsonArray
     */
    fun jsonArray(vararg elements: Any): JsonArray {
        return JsonArray(elements.toList())
    }

}