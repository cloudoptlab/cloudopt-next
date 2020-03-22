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

import io.vertx.core.http.HttpHeaders
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.config.ConfigManager
import org.beetl.core.Configuration
import org.beetl.core.GroupTemplate
import org.beetl.core.Template
import org.beetl.core.resource.ClasspathResourceLoader

/*
 * @author: Cloudopt
 * @Time: 2018/1/10
 * @Description: Beetl Render
 */
class BeetlRender : Render {

    companion object {
        @JvmStatic
        open var config: Configuration = Configuration.defaultConfiguration()

        @JvmStatic
        private val templates = mutableMapOf<String, Template>()

        private val resourceLoader = ClasspathResourceLoader(ConfigManager.config.templates)

        open val gt = GroupTemplate(resourceLoader, config)
    }

    override fun render(resource: Resource, obj: Any) {

        val view: View = obj as View

        if (view.view.indexOf(".") < 0) {
            view.view = view.view + ".btl"
        }

        val t = if (templates.get(view.view) != null) {
            templates.get(view.view)
        } else {
            templates.put(view.view, gt.getTemplate(view.view))
            templates.get(view.view)
        }

        t?.binding(view.parameters)

        t?.binding("resource", resource)

        val html = t?.render()

        resource.response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")

        end(resource, html ?: "")
    }

}
