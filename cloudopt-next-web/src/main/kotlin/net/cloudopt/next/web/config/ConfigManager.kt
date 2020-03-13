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
package net.cloudopt.next.web.config

import net.cloudopt.next.utils.Beaner
import net.cloudopt.next.utils.Maper
import net.cloudopt.next.utils.Resourcer
import net.cloudopt.next.yaml.Yamler


/*
 * @author: Cloudopt
 * @Time: 2018/1/15
 * @Description: Used to manage the configuration file
 */
object ConfigManager {

    val DEVYML = "application-dev.yml"

    val PROYML = "application-pro.yml"

    val YML = "application.yml"

    @JvmStatic
    var vertxConfig: VertxConfigBean = VertxConfigBean()

    @JvmStatic
    var webConfig: WebConfigBean = WebConfigBean()

    @JvmStatic
    var wafConfig: WafConfigBean = WafConfigBean()

    init {
        webConfig = init("web", WebConfigBean::class.java) as WebConfigBean
        vertxConfig = init("vertx", VertxConfigBean::class.java) as VertxConfigBean
        wafConfig = init("waf", WafConfigBean::class.java) as WafConfigBean
    }

    @JvmStatic
    open fun init(name: String, clazz: Class<*>): Any {

        var map = Maper.toMap(Beaner.newInstance(clazz))?.toMutableMap() ?: mutableMapOf()

        return Maper.toObject(initMap(name, map), clazz)!!

    }

    @JvmOverloads
    open fun initMap(name: String, map: MutableMap<String, Any> = mutableMapOf()): MutableMap<String, Any> {
        if (Yamler.read(YML, "net.cloudopt.next." + name) != null) {
            map?.putAll(Yamler.read(YML, "net.cloudopt.next." + name) as Map<out String, Any>)
        }

        var dev = if (name.equals("web")) {
            map.get("dev").toString().toBoolean()
        } else {
            ConfigManager.webConfig.dev
        }

        if (Resourcer.exist(DEVYML)) {
            if (dev && Yamler.read(DEVYML, "net.cloudopt.next." + name) != null) {
                map.putAll(Yamler.read(DEVYML, "net.cloudopt.next." + name) as Map<out String, Any>)
            }
        }

        if (Resourcer.exist(PROYML)) {
            if (!dev && Yamler.read(PROYML, "net.cloudopt.next." + name) != null) {
                map.putAll(Yamler.read(PROYML, "net.cloudopt.next." + name) as Map<out String, Any>)
            }
        }

        return map
    }

}