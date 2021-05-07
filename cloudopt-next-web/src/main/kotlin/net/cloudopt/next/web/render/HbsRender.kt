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
import net.cloudopt.next.core.Worker.await
import net.cloudopt.next.core.Worker.global
import net.cloudopt.next.web.NextServer
import net.cloudopt.next.web.Resource
import java.io.FileNotFoundException

class HbsRender : Render {

    companion object {
        @JvmStatic
        private val templates = mutableMapOf<String, Template>()
    }

    override fun render(resource: Resource, obj: Any) {

        var nextTemplate: net.cloudopt.next.web.render.Template = obj as net.cloudopt.next.web.render.Template

        val handlebars = Handlebars()

        global {
            var html = await<String> { promise ->
                val template = if (templates.containsKey(nextTemplate.name)) {
                    templates[nextTemplate.name]
                } else {
                    try {
                        handlebars.compile(NextServer.webConfig.templates + "/" + nextTemplate.name)
                    } catch (e: FileNotFoundException) {
                        promise.fail("The specified page file could not be found: ${nextTemplate.name}!")
                        end(resource, "The specified page file could not be found: ${nextTemplate.name}!")
                        return@await
                    } catch (e: Exception) {
                        promise.fail(e)
                        resource.fail(500)
                        return@await
                    }
                }
                promise.complete(template?.apply(nextTemplate.parameters))
            }
            resource.response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")
            end(resource, html)
        }


    }
}
