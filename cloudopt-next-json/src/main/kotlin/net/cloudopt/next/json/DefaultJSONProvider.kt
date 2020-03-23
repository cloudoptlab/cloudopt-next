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
package net.cloudopt.next.json

import com.alibaba.fastjson.JSON
import net.cloudopt.next.utils.Resourcer

/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Default JsonProvider
 */
class DefaultJSONProvider : JsonProvider {
    override fun toJsonString(obj: Any): String {
        return JSON.toJSONString(obj)
    }

    override fun toJsonMap(jsonString: String): MutableMap<String, Any> {
        return JSON.parseObject(jsonString).toMutableMap()
    }

    override fun toObject(jsonString: String, clazz: Class<*>): Any {
        return JSON.parseObject(jsonString, clazz)
    }

    override fun toJsonMapList(s: String): MutableList<MutableMap<String, Any>> {
        return JSON.parseArray(s).toMutableList() as MutableList<MutableMap<String, Any>>
    }

    override fun toObjectList(jsonString: String, clazz: Class<*>): MutableList<Any> {
        return JSON.parseArray(jsonString, clazz).toMutableList()
    }

    override fun read(filePath: String): MutableMap<String, Any> {
        var jsonString = Resourcer.inputSreamToString(Resourcer.getFileInputStream(filePath))
        jsonString = cleanText(jsonString)
        return toJsonMap(jsonString)
    }

    override fun read(filePath: String, prefix: String): MutableMap<String, Any> {
        var jsonString = Resourcer.inputSreamToString(Resourcer.getFileInputStream(filePath))
        jsonString = cleanText(jsonString)
        var jsonObj = JSON.parseObject(jsonString)
        var list = prefix.split(".")
        for (key in list) {
            if (jsonObj.getJSONObject(key) != null) {
                jsonObj = jsonObj.getJSONObject(key)
            }
        }
        return jsonObj.toMutableMap()
    }

    override fun <T> read(filePath: String, prefix: String, clazz: Class<T>): Any {
        var jsonString = Resourcer.inputSreamToString(Resourcer.getFileInputStream(filePath))
        jsonString = cleanText(jsonString)
        var jsonObj = JSON.parseObject(jsonString)
        var list = prefix.split(".")
        for (key in list) {
            if (jsonObj.getJSONObject(key) != null) {
                jsonObj = jsonObj.getJSONObject(key)
            }
        }
        return jsonObj.toJavaObject(clazz)!!
    }

    private fun cleanText(jsonString: String): String {
        return jsonString.replace("/n", "")
    }


}