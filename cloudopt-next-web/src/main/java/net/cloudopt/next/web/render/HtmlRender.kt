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
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.yaml.Yamler

import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException

/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Text Render
 */
class HtmlRender : Render {

    override fun render(response: HttpServerResponse, result: Any) {
        val view = result as View

        if(view.view.indexOf(".") < 0){
            view.view = view.view + ".html"
        }

        try {
            val bufferedReader = BufferedReader(FileReader(Yamler.getRootClassPath() + "/" + ConfigManager.webConfig.webroot + "/" + view.view))
            val stringBuilder = StringBuilder()
            var content: String
            bufferedReader.forEachLine { content->
                if (content.isNotBlank()){
                    stringBuilder.append(content)
                }
            }
            response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")
            response.end(stringBuilder.toString())

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            response.end()
        } catch (e: IOException) {
            e.printStackTrace()
            response.end()
        }


    }
}
