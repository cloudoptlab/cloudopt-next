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
package net.cloudopt.next.web.config

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions

/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Cloudopt Next Web configuration file
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class WebConfigBean(
    var env: String = "",
    var debug: Boolean = true,
    var banner: Boolean = true,
    var bannerName: String = "",
    var showRoute: Boolean = true,
    var cors: Boolean = true,
    var packageName: String = "",
    var port: Int = 8080,
    var staticPackage: String = "static",
    var templates: String = "templates",
    var errorHandler: String = "net.cloudopt.next.web.handler.DefaultErrorHandler",
    var jsonProvider: String = "net.cloudopt.next.json.DefaultJSONProvider",
    var indexPage: String = "index.html",
    var cookieCors: Boolean = false,
    var bodyLimit: Long = 50L * 1024 * 1024,
    var timeout: Long = 2L * 60 * 1000,
    var httpServerOptions: HttpServerOptions = HttpServerOptions(),
    var socket: SockJSHandlerOptions = SockJSHandlerOptions()
)
