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

import io.vertx.core.http.HttpHeaders
import net.cloudopt.next.web.Welcomer
import java.util.*
import kotlin.math.abs

/*
 * @author: Cloudopt
 * @Time: 2018/1/15
 * @Description: Intercepting error requests
 */
class DefaultErrorHandler : ErrorHandler() {


    override fun handle() {
        if (abs(errorStatusCode) == 404) {
            response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")
            context.response().end(Welcomer.notFound())
            return
        }

        if (abs(errorStatusCode) == 500) {
            response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")
            context.response().end(Welcomer.systemError())
            return
        }
    }

    private fun restult(error: String, errorMessage: String): HashMap<String, String> {
        val map = hashMapOf<String, String>()
        map["error"] = error
        map["errorMessage"] = errorMessage
        return map
    }


    private val errorStatusCode: Int
        get() {
            if (this.context.statusCode() > 0) {
                this.response.statusCode = this.context.statusCode()
            } else {
                this.response.statusCode = 500
            }
            return this.response.statusCode
        }
}
