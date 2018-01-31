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

import java.util.HashMap

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

    @JvmStatic private var renderMap = object : HashMap<String, Render>() {
        init {
            put(JSON, JsonRender())
            put(TEXT, TextRender())
            put(HBS, HbsRender())
            put(BEETL, BeetlRender())
            put(FREE,FreemarkerRender())
            put(HTML,HtmlRender())
        }
    }

    fun getDefaultRender(): Render {
        return get(defaultRender)
    }

    fun setDefaultRender(name:String) {
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
