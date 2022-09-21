/*
 * Copyright 2017-2021 Cloudopt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cloudopt.next.web.handler

import io.vertx.core.http.HttpHeaders
import net.cloudopt.next.web.Welcomer
import kotlin.math.abs

class DefaultErrorHandler : ErrorHandler() {


    override suspend fun handle(statusCode: Int, throwable: Throwable?) {
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
        val errorMessage = if ((throwable?.message ?: "").isNotBlank()) {
            throwable?.message ?: ""
        } else if (context.data().containsKey("errorMessage")) {
            context.data()["errorMessage"].toString()
        } else {
            "This is a bad http request, please check if the parameters match the requirements."
        }
        renderJson(creatResult(errorStatusCode.toString(), errorMessage))
    }

    private fun creatResult(error: String, errorMessage: String): HashMap<String, String> {
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
