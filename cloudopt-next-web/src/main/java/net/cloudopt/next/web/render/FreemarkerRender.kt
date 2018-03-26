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
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.yaml.Yamler
import java.io.File
import java.io.StringWriter

/*
 * @author: Cloudopt
 * @Time: 2018/1/10
 * @Description: Freemarker Render
 */
class FreemarkerRender : Render {

    companion object {

        @JvmStatic
        open var config: Configuration? = null

        @JvmStatic
        open var contentType = "text/html;charset=utf-8"

    }

    init {

        try {
            if (Class.forName("freemarker.template.Configuration") != null) {

                config = Configuration(Configuration.VERSION_2_3_27)

                config?.defaultEncoding = "UTF-8"

                config?.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER

                config?.logTemplateExceptions = false

                config?.numberFormat = "#0.#####"

                config?.dateFormat = "yyyy-MM-dd"

                config?.timeFormat = "HH:mm:ss"

                config?.dateTimeFormat = "yyyy-MM-dd HH:mm:ss"

                config?.setClassForTemplateLoading(FreemarkerRender::class.java, "/" + ConfigManager.webConfig.webroot)

            }
        } catch (e: Exception) {

        }

    }

    override fun render(resource: Resource, obj: Any) {

        var view: View = obj as View

        if (view.view.indexOf(".") < 0) {
            view.view = view.view + ".ftl"
        }

        var temp = config?.getTemplate(view.view)

        var writer = StringWriter()

        temp?.process(view.parameters, writer);

        resource.response.putHeader(HttpHeaders.CONTENT_TYPE, contentType)

        end(resource, writer.toString())
    }

}

