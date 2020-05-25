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

import io.vertx.core.json.Json
import net.cloudopt.next.json.Jsoner
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.utils.Maper


/*
 * @author: Cloudopt
 * @Time: 2018/1/15
 * @Description: Used to manage the configuration file
 */
object ConfigManager {

    private const val CONFIG_JSON_FILENAME = "application.json"

    @JvmStatic
    var config: WebConfigBean = WebConfigBean()

    // Init web config
    var configMap: MutableMap<String, Any> = mutableMapOf()

    val logger = Logger.getLogger(ConfigManager::class.java)

    init {

        config.vertx.maxWorkerExecuteTime = 60L * 1000 * 1000000

        config.vertx.fileSystemOptions.isFileCachingEnabled = false

        config.vertx.blockedThreadCheckInterval = 1000

        config.vertx.maxEventLoopExecuteTime = 2L * 1000 * 1000000

        config.vertx.warningExceptionTime = 5L * 1000 * 1000000

        try{
            configMap = Jsoner.read(CONFIG_JSON_FILENAME)
        }catch (e:RuntimeException){
            logger.warn("[COFIG] Configuration we not found!")
        }

        config = Maper.toObject(configMap, WebConfigBean::class.java) as WebConfigBean

        if (config.env.isNotBlank()) {
            val newConfigFileName = "application-${config.env}.json"
            configMap.putAll(Jsoner.read(newConfigFileName))
        }

    }

    /**
     * Get the data in the configuration file according to the specified prefix and convert it to a map object.
     * @param prefix prefix name
     * @return MutableMap<String, Any>
     */
    @JvmStatic
    open fun init(prefix: String): MutableMap<String, Any> {
        var newMap = configMap
        for (key in prefix.split(".")){
            newMap = configMap.get(key) as MutableMap<String, Any>
        }
        return newMap
    }

    /**
     * Get the data in the configuration file according to the specified prefix and convert to the specified object.
     * @param prefix prefix name
     * @param clazz class name
     * @return MutableMap<String, Any>
     */
    @JvmStatic
    open fun initObject(prefix: String,clazz:Class<*>): Any {
        return Jsoner.toObject(Jsoner.toJsonString(init(prefix)),clazz)
    }
}
