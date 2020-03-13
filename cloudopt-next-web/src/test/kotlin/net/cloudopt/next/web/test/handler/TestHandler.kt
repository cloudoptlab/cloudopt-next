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
package net.cloudopt.next.web.test.handler

import net.cloudopt.next.logging.Logger
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.handler.AutoHandler
import net.cloudopt.next.web.handler.Handler
import kotlin.math.log


/*
 * @author: Cloudopt
 * @Time: 2018/1/26
 * @Description: Test Handler
 */
class TestHandler : Handler() {

    companion object {
        val logger = Logger.getLogger(TestHandler::class.java)
    }

    override fun preHandle(resource: Resource) {
        logger.info(resource.request.absoluteURI() + "-preHandle")
    }

    override fun postHandle(resource: Resource) {
        logger.info(resource.request.absoluteURI() + "-postHandle")
    }

    override fun afterCompletion(resource: Resource) {
        logger.info(resource.request.absoluteURI() + "-afterCompletion")
    }



}