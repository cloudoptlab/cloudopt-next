/*
 * Copyright 2017 Cloudopt.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package net.cloudopt.next.utils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.serializer.SerializerFeature
import java.lang.reflect.Modifier
import java.util.LinkedHashMap


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
    fun toObject(map: MutableMap<String, Any>, beanClass: Class<*>): Any {
        val obj = beanClass.newInstance()
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
                }catch (e:RuntimeException){
                    var jsonObject:JSONObject = map[field.name] as JSONObject
                    field.set(obj, toObject(jsonObject.toMutableMap(),field.type))
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
        val map = LinkedHashMap<String,Any>()
        val clazz = obj.javaClass
        for (field in clazz.declaredFields) {
            field.isAccessible = true;
            var fieldName = field.getName();
            var value = field.get(obj);
            if (value != null){
                map[fieldName] = value;
            }

        }
        return map.toMutableMap()
    }

}