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
package net.cloudopt.next.web.json

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
     * Output json object.
     * @param s Json string
     * @return Json object
     */
    fun toJsonObject(s: String): Any

    /**
     * Output json object.
     * @param s Json string
     * @param clazz Java class
     * @return Json object
     */
    fun toJsonObject(s: String, clazz: Class<*>): Any

    /**
     * Output json array.
     * @param s Json string
     * @param clazz Java class
     * @return Json array
     */
    fun toJsonArray(s: String): Any

    /**
     * Output json array.
     * @param s Json string
     * @param clazz Java class
     * @return Json array
     */
    fun toJsonArray(s: String, clazz: Class<*>): Any

}