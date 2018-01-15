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
import org.beetl.core.Configuration
import org.beetl.core.GroupTemplate
import org.beetl.core.resource.ClasspathResourceLoader

/*
 * @author: Cloudopt
 * @Time: 2018/1/10
 * @Description: Beetl Render
 */
class BeetlRender : Render {

    override fun render(response: HttpServerResponse, obj: Any) {

        var view:View = obj as View

        var resourceLoader = ClasspathResourceLoader()

        var cfg: Configuration = Configuration.defaultConfiguration()

        var gt = GroupTemplate(resourceLoader, cfg)

        var t = gt.getTemplate(view.view)

        t.binding(view.parameters)

        var html = t.render()

        response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")

        response.end(html)
    }

}
