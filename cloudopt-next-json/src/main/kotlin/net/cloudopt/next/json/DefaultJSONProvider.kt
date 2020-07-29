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

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.parser.ParserConfig
import net.cloudopt.next.utils.Resourcer

/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Default JsonProvider.
 */
class DefaultJSONProvider : JsonProvider {

    init {
        val cfg = ParserConfig.getGlobalInstance()
        cfg.addAccept("net.cloudopt.next.")
    }

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

    override fun toList(jsonString: String): MutableList<Any> {
        return JSON.parseArray(jsonString).toMutableList()
    }

    override fun read(filePath: String): MutableMap<String, Any> {
        var jsonString = Resourcer.inputStreamToString(Resourcer.getFileInputStream(filePath))
        jsonString = cleanText(jsonString)
        return toJsonMap(jsonString)
    }

    override fun read(filePath: String, prefix: String): MutableMap<String, Any> {
        var jsonString = Resourcer.inputStreamToString(Resourcer.getFileInputStream(filePath))
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
        var jsonObj = read(filePath, prefix)
        return JSONObject(jsonObj).toJavaObject(clazz)!!
    }

    private fun cleanText(jsonString: String): String {
        return jsonString.replace("/n", "")
    }


}