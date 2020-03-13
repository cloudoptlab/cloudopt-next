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
package net.cloudopt.next.web.render

import io.vertx.core.http.HttpServerResponse
import net.cloudopt.next.web.CloudoptServer
import net.cloudopt.next.web.Resource

/*
 * @author: Cloudopt
 * @Time: 2018/1/10
 * @Description: Render Interface
 */
interface Render {

    /**
     * Output html fragment.
     * @param resource Resource object
     * @param obj May be the view object may also be just a simple text
     */
    fun render(resource: Resource, obj: Any)

    fun end(resource: Resource) {
        CloudoptServer.handlers.forEach { handler ->
            handler.afterCompletion(resource)
        }
        resource.response.end()
    }

    fun end(resource: Resource, text: String) {
        CloudoptServer.handlers.forEach { handler ->
            handler.afterCompletion(resource)
        }
        resource.response.end(text)
    }

}
