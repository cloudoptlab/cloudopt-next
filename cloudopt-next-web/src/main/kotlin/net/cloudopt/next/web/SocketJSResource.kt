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

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Cookie
import io.vertx.ext.web.FileUpload
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.sockjs.SockJSSocket
import net.cloudopt.next.json.Jsoner
import net.cloudopt.next.utils.Maper
import net.cloudopt.next.web.render.RenderFactory
import net.cloudopt.next.web.render.View
import java.util.*

/*
 * @author: Cloudopt
 * @Time: 2020/4/1
 * @Description: Socket Resource
 */
open interface SocketJSResource {

    fun handler(socket:SockJSSocket)

}
