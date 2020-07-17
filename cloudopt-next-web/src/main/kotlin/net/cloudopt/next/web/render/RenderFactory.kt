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

import java.util.*

/*
 * @author: Cloudopt
 * @Time: 2018/1/10
 * @Description: Render Factory
 */
object RenderFactory {

    val JSON = "json"
    val TEXT = "text"
    val HBS = "hbs"
    val BEETL = "beetl"
    val FREE = "freemarker"
    val HTML = "html"

    private var defaultRender = JSON

    @JvmStatic
    private var renderMap = object : HashMap<String, Render>() {
        init {
            put(JSON, JsonRender())
            put(TEXT, TextRender())
            put(HBS, HbsRender())
            put(FREE, FreemarkerRender())
            put(HTML, HtmlRender())
        }
    }

    fun getDefaultRender(): Render {
        return get(defaultRender)
    }

    fun setDefaultRender(name: String) {
        defaultRender = name
    }

    fun add(extension: String, render: Render) {
        renderMap.put(extension, render)
    }

    fun addDefault(name: String, render: Render) {
        renderMap.put(name, render)
        defaultRender = name
    }

    fun get(name: String): Render {
        var render = renderMap[name]
        return render ?: renderMap[defaultRender] as Render
    }

    fun contains(name: String): Boolean {
        return renderMap.containsKey(name)
    }

}
