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

import java.util.*

/*
 * @author: Cloudopt
 * @Time: 2018/1/15
 * @Description: Intercepting error requests
 */
class DefaultErrorHandler : Handler() {


    override fun handle() {
        if (Math.abs(errorStatusCode) == 404) {
            var json = restult("404", "[CLOUDOPT-NEXT] Resource not found!")
            renderJson(json)
        }

        if (Math.abs(errorStatusCode) == 500) {
            var json = restult("500", "[CLOUDOPT-NEXT] Internal error!")
            renderJson(json)
        }
    }

    fun restult(error: String, errorMessage: String): HashMap<String, String> {
        var map = hashMapOf<String, String>()
        map.put("error", error)
        map.put("errorMessage", errorMessage)
        return map
    }


    val errorStatusCode: Int
        get() {
            if (this.context?.statusCode() ?: 0 > 0) {
                this.response?.statusCode = this.context?.statusCode()!!
            }else{
                this.response?.statusCode = 500
            }

            return this.response?.statusCode
        }
}
