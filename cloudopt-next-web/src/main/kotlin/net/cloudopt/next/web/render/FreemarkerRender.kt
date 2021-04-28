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

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import io.vertx.core.http.HttpHeaders
import net.cloudopt.next.web.Resource
import net.cloudopt.next.core.Worker.await
import net.cloudopt.next.core.Worker.global
import net.cloudopt.next.core.ConfigManager
import net.cloudopt.next.web.NextServer
import java.io.StringWriter

class FreemarkerRender : Render {

    companion object {

        @JvmStatic
        open var config: Configuration? = null

        @JvmStatic
        open var contentType = "text/html;charset=utf-8"

        @JvmStatic
        private val templates = mutableMapOf<String, Template?>()

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

                config?.setClassForTemplateLoading(FreemarkerRender::class.java, "/" + NextServer.webConfig.templates)

            }
        } catch (e: Exception) {

        }

    }

    override fun render(resource: Resource, obj: Any) {

        val nextTemplate: net.cloudopt.next.web.render.Template = obj as net.cloudopt.next.web.render.Template

        if (nextTemplate.name.indexOf(".") < 0) {
            nextTemplate.name = nextTemplate.name + ".ftl"
        }

        global {
            val html = await<String> { promise ->
                try {
                    var temp = if (templates.containsKey(nextTemplate.name)) {
                        templates[nextTemplate.name]
                    } else {
                        templates[nextTemplate.name] = config?.getTemplate(nextTemplate.name)
                        templates[nextTemplate.name]
                    }
                    var writer = StringWriter()
                    temp?.process(nextTemplate.parameters, writer)
                    resource.response.putHeader(HttpHeaders.CONTENT_TYPE, contentType)
                    promise.complete(writer.toString())
                } catch (e: Exception) {
                    promise.fail(e)
                    resource.fail(500)
                    return@await
                }
            }
            end(resource, html)
        }


    }

}

