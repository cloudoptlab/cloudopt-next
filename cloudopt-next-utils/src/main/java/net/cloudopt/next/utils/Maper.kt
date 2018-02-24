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
package net.cloudopt.next.aop

import com.alibaba.fastjson.JSON
import java.beans.Introspector
import java.util.HashMap
import kotlin.reflect.KClass


/*
 * @author: Cloudopt
 * @Time: 2018/1/5
 * @Description: This is used for map to object conversion
 */
object Maper{

    /**
     * It will convert map to object
     * @param map Need to convert the map
     * @param beanClass The type after the conversion
     * @return The object after the conversion is completed
     */
    fun toObject(map: Map<String, Any>?, beanClass: Class<*>): Any? {
        return JSON.parseObject(JSON.toJSONString(map),beanClass)
    }

    /**
     * It will convert object to map
     * @param obj Need to convert the object
     * @return The map after the conversion is completed
     */
    fun toMap(obj: Any?): HashMap<String, Any>? {
        return JSON.parseObject(JSON.toJSONString(obj),Map::class.java) as HashMap<String,Any>
    }

}