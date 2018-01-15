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

import freemarker.template.Configuration
import io.vertx.core.http.HttpServerResponse
import freemarker.template.TemplateExceptionHandler
import io.vertx.core.http.HttpHeaders
import java.io.File
import java.io.StringWriter

/*
 * @author: Cloudopt
 * @Time: 2018/1/10
 * @Description: Freemarker Render
 */
class FreemarkerRender : Render {

    val cfg:Configuration = Configuration(Configuration.VERSION_2_3_27)

    init {

        cfg.setDirectoryForTemplateLoading(File("/"))

        cfg.setDefaultEncoding("UTF-8")

        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)

        cfg.setLogTemplateExceptions(false)

    }

    override fun render(response: HttpServerResponse, obj: Any) {

        var view:View = obj as View

        var temp = cfg.getTemplate(view.view)

        var writer = StringWriter()

        temp.process(view.parameters, writer);

        response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")

        response.end(writer.toString())
    }

}
