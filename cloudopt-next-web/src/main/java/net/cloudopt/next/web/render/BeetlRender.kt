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
import io.vertx.core.http.HttpServerResponse
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.config.ConfigManager
import org.beetl.core.Configuration
import org.beetl.core.GroupTemplate
import org.beetl.core.resource.ClasspathResourceLoader

/*
 * @author: Cloudopt
 * @Time: 2018/1/10
 * @Description: Beetl Render
 */
class BeetlRender : Render {

    companion object {
        @JvmStatic
        open var config: Configuration? = null
    }

    override fun render(resource: Resource, obj: Any) {

        if (config == null) {
            config = Configuration.defaultConfiguration()
        }

        var view: View = obj as View

        if (view.view.indexOf(".") < 0) {
            view.view = view.view + ".btl"
        }

        var resourceLoader = ClasspathResourceLoader(ConfigManager.webConfig.webroot)

        var gt = GroupTemplate(resourceLoader, config)

        var t = gt.getTemplate(view.view)

        t.binding(view.parameters)

        var html = t.render()

        resource.response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")

        end(resource, html)
    }

}
