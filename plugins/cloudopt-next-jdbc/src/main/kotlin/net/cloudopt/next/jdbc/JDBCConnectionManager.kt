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
package net.cloudopt.next.jdbc

import net.cloudopt.next.core.Classer
import net.cloudopt.next.core.ConfigManager
import net.cloudopt.next.json.Jsoner.jsonToObjectList
import net.cloudopt.next.json.Jsoner.toJsonString
import java.sql.Connection
import javax.sql.DataSource
import kotlin.reflect.full.createInstance

object JDBCConnectionManager {
    var connectionMap: MutableMap<String, Connection> = mutableMapOf()
    var dataSourceMap: MutableMap<String, DataSource> = mutableMapOf()
    var jdbcConfigMap: MutableMap<String, JDBCConfig> = mutableMapOf()

    init {
        if (ConfigManager.configMap.contains("datasource")) {
            val jdbcConfigList: MutableList<JDBCConfig> =
                ConfigManager.configMap["datasource"]!!.toJsonString().jsonToObjectList(JDBCConfig::class)
            for (config in jdbcConfigList) {
                if (config.name.isNotBlank()) {
                    jdbcConfigMap[config.name] = config
                    val jdbcConnectionPool: JDBCConnectionPool =
                        Classer.loadClass(config.pool).createInstance() as JDBCConnectionPool
                    jdbcConnectionPool.init(config)
                    connectionMap[config.name] = jdbcConnectionPool.getConnection()
                    dataSourceMap[config.name] = jdbcConnectionPool.getDatasource()
                } else {
                    throw RuntimeException("A datasource name is detected as empty!")
                }
            }


        } else {
            throw RuntimeException("No database related configuration was found in the configuration to initialize!")
        }

    }
}