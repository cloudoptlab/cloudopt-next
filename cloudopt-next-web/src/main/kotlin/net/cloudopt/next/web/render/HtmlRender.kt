/*
 * Copyright 2017-2020 original authors
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
package net.cloudopt.next.web.render

import io.vertx.core.http.HttpHeaders
import net.cloudopt.next.logging.Logger
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
        val logger = Logger.getLogger(HtmlRender.javaClass)
    }

    override fun render(resource: Resource, result: Any) {
        val view = result as View

        if (view.view.indexOf(".") < 0) {
            view.view = view.view + ".html"
        }

        try {
            resource.response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")
            if (templates.get(view.view) != null) {
                end(resource, templates.get(view.view) ?: "")
            } else {
                var inputStream = Resourcer.getFileInputStream(ConfigManager.config.templates + "/" + view.view)
                var bufferedReader = BufferedReader(InputStreamReader(inputStream))
                var stringBuilder = StringBuilder()
                bufferedReader.forEachLine { content ->
                    if (content.isNotBlank()) {
                        stringBuilder.append(content)
                    }
                }
                templates.put(view.view, stringBuilder.toString())
                end(resource, stringBuilder.toString())
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            end(resource)
        } catch (e: IOException) {
            e.printStackTrace()
            end(resource)
        }


    }
}
