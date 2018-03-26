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

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpServerResponse
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.config.ConfigManager

import java.io.IOException

/*
 * @author: Cloudopt
 * @Time: 2018/1/10
 * @Description: Hbs Render
 */
class HbsRender : Render {

    override fun render(resource: Resource, obj: Any) {

        var view: View = obj as View

        val handlebars = Handlebars()

        var template: Template? = null

        var html = ""

        try {
            template = handlebars.compile(ConfigManager.webConfig.webroot + "/" + view.view)
            html = template!!.apply(view.parameters)
        } catch (e: IOException) {
            e.printStackTrace()
            end(resource)
        }

        resource.response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")

        end(resource, html)

    }
}
