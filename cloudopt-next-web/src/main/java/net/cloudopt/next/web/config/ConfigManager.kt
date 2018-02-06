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

import net.cloudopt.next.aop.Beaner
import net.cloudopt.next.aop.Maper
import net.cloudopt.next.yaml.Yamler
import kotlin.reflect.KClass


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
    val vertxConfig: VertxConfigBean = init("vertx", VertxConfigBean::class.java) as VertxConfigBean

    @JvmStatic
    val webConfig: WebConfigBean = init("web", WebConfigBean::class.java) as WebConfigBean

    @JvmStatic
    val wafConfig: WafConfigBean = init("waf", WafConfigBean::class.java) as WafConfigBean

    @JvmStatic
    open fun init(name: String, clazz: Class<*>): Any {

        var map = Maper.toMap(Beaner.newInstance(clazz))?.toMutableMap() ?: mutableMapOf()

        return Maper.toObject(initMap(name, map), clazz)!!

    }

    @JvmOverloads
    open fun initMap(name: String, map: MutableMap<String, Any> = mutableMapOf()): MutableMap<String, Any> {

        try {

            if (Yamler.read(YML, "net.cloudopt.next." + name) != null) {
                map?.putAll(Yamler.read(YML, "net.cloudopt.next." + name) as Map<out String, Any>)
            }

            var dev = map?.get("dev") as Boolean

            if (dev && Yamler.read(DEVYML, "net.cloudopt.next." + name) != null) {
                map.putAll(Yamler.read(DEVYML, "net.cloudopt.next." + name) as Map<out String, Any>)
            }

            if (!dev && Yamler.read(PROYML, "net.cloudopt.next." + name) != null) {
                map.putAll(Yamler.read(PROYML, "net.cloudopt.next." + name) as Map<out String, Any>)
            }

        } catch (e: RuntimeException) {

        }

        return map

    }

}