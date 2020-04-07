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

/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: JsonProvider Interface
 */
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
    fun toObject(jsonString: String, clazz: Class<*>): Any

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
    fun toObjectList(jsonString: String, clazz: Class<*>): MutableList<Any>


    /**
     * Read Json file to json string
     * @param filePath File path
     * @return MutableMap<String,Any>
     */
    fun read(filePath: String): MutableMap<String, Any>

    /**
     * Read Json file to Map
     * @param filePath File path
     * @param prefix Attribute prefix in json file
     * @return map
     */
    fun read(filePath: String, prefix: String): MutableMap<String, Any>


    /**
     * Read Json file into a specific instance of the object
     * @param filePath File path
     * @param prefix Attribute prefix in json file
     * @param clazz Class name
     * @return Object
     */
    fun <T> read(filePath: String, prefix: String, clazz: Class<T>): Any

}