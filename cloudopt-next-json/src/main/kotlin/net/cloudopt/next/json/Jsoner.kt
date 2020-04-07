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

import net.cloudopt.next.utils.Beaner
import net.cloudopt.next.utils.Classer


/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: JsonProvider Interface
 */

object Jsoner {

    @JvmStatic
    var jsonProvider: JsonProvider = Beaner.newInstance(Classer.loadClass("net.cloudopt.next.json.DefaultJSONProvider"))

    fun toJsonString(obj: Any): String {
        return jsonProvider.toJsonString(obj)
    }

    fun toJsonMap(s: String): MutableMap<String, Any> {
        return jsonProvider.toJsonMap(s)
    }

    fun toJsonMapList(s: String): MutableList<MutableMap<String, Any>> {
        return jsonProvider.toJsonMapList(s)
    }

    fun toObject(s: String, clazz: Class<*>): Any {
        return jsonProvider.toObject(s, clazz)
    }

    fun toObjectList(s: String, clazz: Class<*>): Any {
        return jsonProvider.toObjectList(s, clazz)
    }

    fun toJsonArray(s: String, clazz: Class<*>): MutableList<Any> {
        return jsonProvider.toObjectList(s, clazz)
    }

    fun read(filePath: String): MutableMap<String, Any> {
        return jsonProvider.read(filePath)
    }

    fun read(filePath: String, prefix: String): MutableMap<String, Any> {
        return jsonProvider.read(filePath, prefix)
    }

    fun <T> read(filePath: String, prefix: String, clazz: Class<T>): Any {
        return jsonProvider.read(filePath, prefix, clazz)
    }


}