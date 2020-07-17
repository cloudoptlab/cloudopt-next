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
package net.cloudopt.next.web.config

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

        try {
            configMap = Jsoner.read(CONFIG_JSON_FILENAME)
        } catch (e: RuntimeException) {
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
        for (key in prefix.split(".")) {
            newMap = newMap.get(key) as MutableMap<String, Any>
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
    open fun initObject(prefix: String, clazz: Class<*>): Any {
        return Jsoner.toObject(Jsoner.toJsonString(init(prefix)), clazz)
    }
}
