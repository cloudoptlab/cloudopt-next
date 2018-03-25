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
package net.cloudopt.next.web.test.interceptor

import net.cloudopt.next.web.Interceptor
import net.cloudopt.next.web.Resource
import org.slf4j.LoggerFactory


/*
 * @author: Cloudopt
 * @Time: 2018/2/28
 * @Description: Test Case
 */
class TestInterceptor : Interceptor {
     val logger = LoggerFactory.getLogger(this::class.java.simpleName)
    override fun intercept(resource: Resource): Boolean {
         logger.info("Through the intercept !")
        return true
    }

    override fun response(resource: Resource): Resource {
        return resource
    }


}