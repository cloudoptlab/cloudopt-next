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

import io.vertx.core.http.HttpHeaders
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.utils.Resourcer
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.Worker.await
import net.cloudopt.next.web.Worker.global
import net.cloudopt.next.web.config.ConfigManager
import java.io.BufferedReader
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
        val logger = Logger.getLogger(HtmlRender::class)
    }

    override fun render(resource: Resource, result: Any) {
        global {
            val nextTemplate = result as Template

            if (nextTemplate.name.indexOf(".") < 0) {
                nextTemplate.name = nextTemplate.name + ".html"
            }
            resource.response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8")
            if (templates.containsKey(nextTemplate.name)) {
                end(resource, templates[nextTemplate.name] ?: "")
            } else {
                val html = await<String> { promise ->
                    val inputStream = try {
                        Resourcer.getFileInputStream(ConfigManager.config.templates + "/" + nextTemplate.name)
                    } catch (e: NullPointerException) {
                        promise.fail("The specified page file could not be found: ${nextTemplate.name}!")
                        end(resource, "The specified page file could not be found: ${nextTemplate.name}!")
                        return@await
                    } catch (e: Exception) {
                        promise.fail(e)
                        end(resource, "")
                        return@await
                    }
                    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    bufferedReader.forEachLine { content ->
                        if (content.isNotBlank()) {
                            stringBuilder.append(content)
                        }
                    }
                    templates[nextTemplate.name] = stringBuilder.toString()
                    promise.complete(stringBuilder.toString())
                }

                end(resource, html)
            }
        }
    }
}
