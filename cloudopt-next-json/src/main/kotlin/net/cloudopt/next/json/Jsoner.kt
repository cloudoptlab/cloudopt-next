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
package net.cloudopt.next.json

import net.cloudopt.next.utils.Classer
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Json's tool class.
 */

object Jsoner {

    @JvmStatic
    var jsonProvider: JsonProvider = Classer.loadClass("net.cloudopt.next.json.DefaultJSONProvider")
        .createInstance() as JsonProvider

    /**
     * Output json string.
     * @param obj Json Object
     * @return Json string
     */
    fun toJsonString(obj: Any): String {
        return jsonProvider.toJsonString(obj)
    }

    /**
     * Output MutableMap
     * @param jsonString Json string
     * @return MutableMap
     */
    fun toJsonMap(jsonString: String): MutableMap<String, Any> {
        return jsonProvider.toJsonMap(jsonString)
    }

    /**
     * Output json object.
     * @param jsonString Json string
     * @param clazz Java class
     * @return Json object
     */
    fun toObject(jsonString: String, clazz: KClass<*>): Any {
        return jsonProvider.toObject(jsonString, clazz)
    }

    /**
     * Output json map list.
     * @param jsonString Json string
     * @return MutableList<MutableMap<String,Any>>
     */
    fun toJsonMapList(jsonString: String): MutableList<MutableMap<String, Any>> {
        return jsonProvider.toJsonMapList(jsonString)
    }

    /**
     * Output obj array.
     * @param jsonString Json string
     * @param clazz Java class
     * @return MutableList<Any>
     */
    fun toObjectList(jsonString: String, clazz: KClass<*>): MutableList<Any> {
        return jsonProvider.toObjectList(jsonString, clazz)
    }

    /**
     * Output any list.
     * @param jsonString Json string
     * @return MutableList<Any>
     */
    fun toList(jsonString: String): MutableList<Any> {
        return jsonProvider.toList(jsonString)
    }

    /**
     * Read Json file to json string
     * @param filePath File path
     * @return MutableMap<String,Any>
     */
    fun read(filePath: String): MutableMap<String, Any> {
        return jsonProvider.read(filePath)
    }

    /**
     * Read Json file to Map
     * @param filePath File path
     * @param prefix Attribute prefix in json file
     * @return map
     */
    fun read(filePath: String, prefix: String): MutableMap<String, Any> {
        return jsonProvider.read(filePath, prefix)
    }

    /**
     * Read Json file into a specific instance of the object
     * @param filePath File path
     * @param prefix Attribute prefix in json file
     * @param clazz Class name
     * @return Object
     */
    fun read(filePath: String, prefix: String, clazz: KClass<*>): Any {
        return jsonProvider.read(filePath, prefix, clazz)
    }


}