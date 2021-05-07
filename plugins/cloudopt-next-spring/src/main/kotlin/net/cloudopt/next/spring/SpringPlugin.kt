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
package net.cloudopt.next.spring

import net.cloudopt.next.core.ConfigManager
import net.cloudopt.next.core.Plugin
import org.springframework.context.ConfigurableApplicationContext
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
        if (map["xml"] != null && map["xml"].toString().isNotBlank()) {
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
