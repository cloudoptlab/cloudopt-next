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
package net.cloudopt.next.web.config

import io.vertx.core.DeploymentOptions
import io.vertx.core.VertxOptions
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions

/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Cloudopt Next Web configuration file
 */
data class WebConfigBean(
    var env: String = "",
    var debug: Boolean = true,
    var banner: Boolean = true,
    var bannerName: String = "banner.txt",
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
    var logColor: Boolean = true,
    var timeout: Long = 2L * 60 * 1000,
    var waf: WafConfigBean = WafConfigBean(),
    var vertx: VertxOptions = VertxOptions(),
    var vertxHttpServer: HttpServerOptions = HttpServerOptions(),
    var vertxDeployment: DeploymentOptions = DeploymentOptions(),
    var socket: SockJSHandlerOptions = SockJSHandlerOptions()
)
