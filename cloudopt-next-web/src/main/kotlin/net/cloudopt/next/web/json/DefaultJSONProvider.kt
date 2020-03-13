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

import com.alibaba.fastjson.JSON

/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Default JsonProvider
 */
class DefaultJSONProvider : JsonProvider {

    override fun toJsonString(obj: Any): String {
        return JSON.toJSONString(obj)
    }

    override fun toJsonObject(s: String): Any {
        return JSON.parseObject(s)
    }

    override fun toJsonObject(s: String, clazz: Class<*>): Any {
        return JSON.parseObject(s, clazz)
    }

    override fun toJsonArray(s: String): Any {
        return JSON.parseArray(s)
    }

    override fun toJsonArray(s: String, clazz: Class<*>): Any {
        return JSON.parseArray(s, clazz)
    }

}