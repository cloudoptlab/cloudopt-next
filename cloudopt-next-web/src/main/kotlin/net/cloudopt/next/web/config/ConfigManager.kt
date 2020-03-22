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

import net.cloudopt.next.json.Jsoner
import net.cloudopt.next.utils.Maper


/*
 * @author: Cloudopt
 * @Time: 2018/1/15
 * @Description: Used to manage the configuration file
 */
object ConfigManager {

    val CONFIG_JSON_FILENAME = "application.json"

    @JvmStatic
    var config: WebConfigBean = WebConfigBean()

    init {
        // Init web config
        var webConfigMap: MutableMap<String, Any> = Jsoner.read(CONFIG_JSON_FILENAME)

        if (config.env.isNotBlank()) {
            val newConfigFileName = "application-${config.env}.json"
            webConfigMap.putAll(Jsoner.read(newConfigFileName))
        }

        config = Maper.toObject(webConfigMap, WebConfigBean::class.java) as WebConfigBean

    }

    @JvmStatic
    open fun init(prefix: String): MutableMap<String, Any> {
        var configMap = Jsoner.read(CONFIG_JSON_FILENAME, prefix)
        if (config.env.isNotBlank()) {
            val newConfigFileName = "application-${config.env}.json"
            configMap.putAll(Jsoner.read(newConfigFileName, prefix))
        }
        return configMap
    }
}
