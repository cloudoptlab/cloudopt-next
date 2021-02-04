/*
 * Copyright 2017-2021 Cloudopt
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

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import io.vertx.core.http.HttpHeaders
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.config.ConfigManager
import java.io.IOException

/*
 * @author: Cloudopt
 * @Time: 2018/1/10
 * @Description: Hbs Render
 */
class HbsRender : Render {

    companion object {
        @JvmStatic
        private val templates = mutableMapOf<String, Template?>()
    }

    override fun render(resource: Resource, obj: Any) {

        var nextTemplate: net.cloudopt.next.web.render.Template = obj as net.cloudopt.next.web.render.Template

        val handlebars = Handlebars()

        var template: Template? = null

        var html = ""

        try {
            template = if (templates[nextTemplate.name] != null) {
                templates[nextTemplate.name]
            } else {
                templates[nextTemplate.name] = handlebars.compile(ConfigManager.config.templates + "/" + nextTemplate.name)
                templates[nextTemplate.name]
            }

            html = template!!.apply(nextTemplate.parameters)
        } catch (e: IOException) {
            e.printStackTrace()
            end(resource)
        }

        resource.response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")

        end(resource, html)

    }
}
