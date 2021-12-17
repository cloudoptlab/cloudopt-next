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
package net.cloudopt.next.jooq

import net.cloudopt.next.core.Plugin
import net.cloudopt.next.jdbc.JDBCConnectionManager
import org.jooq.SQLDialect
import org.jooq.conf.SettingsTools
import org.jooq.impl.DSL
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultTransactionProvider
import java.sql.SQLException


class JooqPlugin : Plugin {

    override fun start(): Boolean {

        JDBCConnectionManager.jdbcConfigMap.forEach { map ->
            val sqlDialect = when (map.value.database) {
                "mysql" -> SQLDialect.MYSQL
                "derby" -> SQLDialect.DERBY
                "firebird" -> SQLDialect.FIREBIRD
                "mariadb" -> SQLDialect.MARIADB
                "postgres" -> SQLDialect.POSTGRES
                "sqlite" -> SQLDialect.SQLITE
                else -> {
                    SQLDialect.MYSQL
                }
            }
            val connectionProvider = DataSourceConnectionProvider(JDBCConnectionManager.dataSourceMap[map.key])
            val transactionProvider = DefaultTransactionProvider(connectionProvider)
            val configuration = DefaultConfiguration()
            val settings = SettingsTools.defaultSettings()
            configuration.set(connectionProvider)
                .set(transactionProvider)
                .set(sqlDialect)
                .set(settings)
            JooqManager.dslMap[map.key] = DSL.using(configuration)

        }
        return true

    }

    override fun stop(): Boolean {
        return try {
            JDBCConnectionManager.dataSourceMap.forEach { map ->
                map.value.connection.close()
            }
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }

    }
}