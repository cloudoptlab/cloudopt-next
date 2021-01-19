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

import com.alibaba.fastjson.JSONObject
import java.lang.reflect.Modifier
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


/*
 * @author: Cloudopt
 * @Time: 2018/1/5
 * @Description: This is used for map to object conversion
 */
object Maper {

    /**
     * It will convert map to object
     * @param map Need to convert the map
     * @param beanClass The type after the conversion
     * @return The object after the conversion is completed
     */
    fun toObject(map: MutableMap<String, Any>, beanClass: KClass<*>): Any {
        val obj = beanClass.createInstance()
        val fields = obj.javaClass.declaredFields
        for (field in fields) {
            val mod: Int = field.modifiers
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue
            }
            field.isAccessible = true
            if (map.containsKey(field.name)) {
                try {
                    field.set(obj, map[field.name])
                } catch (e: RuntimeException) {
                    var jsonObject: JSONObject = map[field.name] as JSONObject
                    field.set(obj, toObject(jsonObject.toMutableMap(), field.type.kotlin))
                }
            }
        }
        return obj
    }

    /**
     * It will convert object to map
     * @param obj Need to convert the object
     * @return The map after the conversion is completed
     */
    fun toMap(obj: Any): MutableMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        val clazz = obj.javaClass
        for (field in clazz.declaredFields) {
            field.isAccessible = true;
            var fieldName = field.getName();
            var value = field.get(obj);
            if (value != null) {
                map[fieldName] = value;
            }

        }
        return map.toMutableMap()
    }

}