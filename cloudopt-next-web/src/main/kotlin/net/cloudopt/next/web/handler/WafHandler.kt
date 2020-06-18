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
 * @Time: 2018/1/26
 * @Description: Used to block common network attacks
 */

@AutoHandler
class WafHandler : Handler() {

    override fun preHandle(resource: Resource) {
        if (ConfigManager.config.waf.plus) {
            resource.setHeader("X-Content-Type-Options", "nosniff")
            resource.setHeader("X-Download-Options", "noopen")
            resource.setHeader("X-XSS-Protection", "1; mode=block")
            resource.setHeader("X-FRAME-OPTIONS", "DENY")
        }
    }

    override fun postHandle(resource: Resource) {
    }

    override fun afterCompletion(resource: Resource) {
    }

}
