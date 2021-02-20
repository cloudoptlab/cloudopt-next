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
    fun String.jsontoMutableMap(): MutableMap<String, Any> {
        return jsonProvider.toJsonMap(this)
    }

    /**
     * Output json object.
     * @param clazz Java class
     * @return Json object
     */
    fun String.jsonToObject(clazz: KClass<*>): Any {
        return jsonProvider.toObject(this, clazz)
    }

    /**
     * Output json map list.
     * @return MutableList<MutableMap<String,Any>>
     */
    fun String.jsontoMutableMapList(): MutableList<MutableMap<String, Any>> {
        return jsonProvider.toJsonMapList(this)
    }

    /**
     * Output obj array.
     * @param clazz Java class
     * @return MutableList<Any>
     */
    fun String.jsonToObjectList(clazz: KClass<*>): MutableList<Any> {
        return jsonProvider.toObjectList(this, clazz)
    }

    /**
     * Output any list.
     * @return MutableList<Any>
     */
    fun String.jsontoObjectList(): MutableList<Any> {
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

}