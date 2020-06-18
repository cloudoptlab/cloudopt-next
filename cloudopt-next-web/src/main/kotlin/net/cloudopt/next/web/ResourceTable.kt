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
package net.cloudopt.next.web

import io.vertx.core.http.HttpMethod
import java.lang.reflect.Method

/*
 * @author: Cloudopt
 * @Time: 2018/1/18
 * @Description: Resource Table
 */

data class ResourceTable(
    var url: String = "", var httpMethod: HttpMethod = HttpMethod.GET,
    var clazz: Class<Resource> = Resource::class.java, var methodName: String = "",
    var blocking: Boolean = false,
    var clazzMethod: Method? = null,
    var parameterTypes: Array<Class<*>> = arrayOf<Class<*>>()
)