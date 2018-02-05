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
package net.cloudopt.next.web.test.controller

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.event.EventManager
import net.cloudopt.next.web.render.View
import net.cloudopt.next.web.route.API
import net.cloudopt.next.web.route.GET
import net.cloudopt.next.web.route.POST


/*
 * @author: Cloudopt
 * @Time: 2018/1/26
 * @Description: Test Controller
 */
@API("/")
class IndexController : Resource() {

    @GET
    fun index() {
        var name = getParam<String>("name") ?: ""
        renderText(name)
    }

    @GET("html")
    fun html() {
        var view = View()
        view.view = "index"
        renderHtml(view)
    }

    @GET("free")
    fun free() {
        var view = View()
        view.view = "index"
        view.parameters.put("name","free")
        renderFree(view)
    }

    @GET("hbs")
    fun hbs() {
        var view = View()
        view.view = "index"
        view.parameters.put("name","hbs")
        renderHbs(view)
    }

    @GET("json")
    fun json() {
        var map = hashMapOf<String,Any>()
        map.put("a",1)
        map.put("b",2)
        renderJson(map)
    }

    @GET("beetl")
    fun beetl() {
        var view = View()
        view.view = "index"
        view.parameters.put("name","beetl")
        renderBeetl(view)
    }

    @GET("event")
    fun event(){
        EventManager.send("net.cloudopt.web.test","This is test message!")
        renderJson("Send Event!")
    }
}