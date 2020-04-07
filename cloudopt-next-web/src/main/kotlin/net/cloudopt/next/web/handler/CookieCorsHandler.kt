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
package net.cloudopt.next.web.handler

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.config.ConfigManager

/*
 * @author: Cloudopt
 * @Time: 2018/1/15
 * @Description: Used to support the use of cookies across domains
 */
@AutoHandler
class CookieCorsHandler : Handler() {
    override fun preHandle(resource: Resource) {
        if (ConfigManager.config.cookieCors) {
            resource.setHeader("Access-Control-Allow-Credentials", "true")
        }
    }

    override fun postHandle(resource: Resource) {
    }

    override fun afterCompletion(resource: Resource) {
    }

}
