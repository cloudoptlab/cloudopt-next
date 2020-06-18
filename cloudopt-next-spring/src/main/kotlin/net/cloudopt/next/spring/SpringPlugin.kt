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
package net.cloudopt.next.spring

import net.cloudopt.next.web.Plugin
import net.cloudopt.next.web.config.ConfigManager
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/*
 * @author: Cloudopt
 * @Time: 2018/2/7
 * @Description: Spring Plugin
 */
class SpringPlugin : Plugin {

    private var configFiles: Array<out String> = arrayOf()
    private var configClasses: Array<out Class<*>> = arrayOf()
    private var context: ConfigurableApplicationContext? = null
    private var map = ConfigManager.init("spring")

    constructor() {
        this.configFiles = arrayOf("classpath:applicationContext.xml")
        if (map.get("xml") != null && map.get("xml").toString().isNotBlank()) {
            var s = map.get("xml").toString()
            configFiles = s.split(",").toTypedArray()
        }
    }

    constructor(vararg configFiles: String) {
        this.configFiles = configFiles
    }

    constructor(context: ConfigurableApplicationContext) {
        this.context = context
    }

    constructor(vararg configClasses: Class<*>) {
        this.configClasses = configClasses
    }

    override fun start(): Boolean {
        if (this.context == null) {
            this.context = ClassPathXmlApplicationContext(*configFiles)
        }
        SpringBuilder.setContext(context)
        return true
    }

    override fun stop(): Boolean {
        SpringBuilder.removeContext()
        return true
    }
}
