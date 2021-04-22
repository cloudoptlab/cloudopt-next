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

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import kotlin.reflect.KClass


class DefaultJSONProvider : JsonProvider {

    init {
        DatabindCodec.mapper().registerModule(JavaTimeModule())
    }

    override fun toJsonString(obj: Any): String {
        return Json.encode(obj)
    }

    override fun toJsonMap(jsonString: String): MutableMap<String, Any> {
        return (Json.decodeValue(jsonString) as JsonObject).map.toMutableMap()
    }

    override fun <T> toObject(jsonString: String, clazz: KClass<*>): T {
        return Json.decodeValue(jsonString, clazz.java) as T
    }

    override fun toJsonMapList(jsonString: String): MutableList<MutableMap<String, Any>> {
        val value = Json.decodeValue(jsonString) as JsonArray
        val mapList = mutableListOf<MutableMap<String, Any>>()
        for (it in value.withIndex()) {
            mapList.add(value.getJsonObject(it.index).map)
        }
        return mapList
    }

    override fun <T> toObjectList(jsonString: String, clazz: KClass<*>): MutableList<T> {
        val value = Json.decodeValue(jsonString) as JsonArray
        val list = mutableListOf<T>()
        for (it in value.withIndex()) {
            list.add(value.getJsonObject(it.index).mapTo(clazz.java) as T)
        }
        return list
    }

    override fun <T> toList(jsonString: String): MutableList<T> {
        return (Json.decodeValue(jsonString) as JsonArray).toMutableList() as MutableList<T>
    }

    override fun toJsonObject(jsonString: String): JsonObject {
        return Json.decodeValue(jsonString) as JsonObject
    }

    override fun toJsonArray(jsonString: String): JsonArray {
        return Json.decodeValue(jsonString) as JsonArray
    }

}