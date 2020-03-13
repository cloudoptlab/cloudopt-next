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
import net.cloudopt.next.utils.Resourcer
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.config.ConfigManager
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Text Render
 */
class HtmlRender : Render {

    companion object {
        @JvmStatic
        private val templates = mutableMapOf<String, String?>()
    }

    override fun render(resource: Resource, result: Any) {
        val view = result as View

        if (view.view.indexOf(".") < 0) {
            view.view = view.view + ".html"
        }

        try {
            var html = if (templates.get(view.view) != null) {
                templates.get(view.view)
            } else {
                var inputStream = Resourcer.getFileInputStream(ConfigManager.webConfig.webroot + "/" + view.view)
                var bufferedReader = BufferedReader(InputStreamReader(inputStream))
                var stringBuilder = StringBuilder()
                bufferedReader.forEachLine { content ->
                    if (content.isNotBlank()) {
                        stringBuilder.append(content)
                    }
                }
                templates.put(view.view, stringBuilder.toString())
            }
            resource.response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")
            end(resource, html ?: "")

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            end(resource)
        } catch (e: IOException) {
            e.printStackTrace()
            end(resource)
        }


    }
}
