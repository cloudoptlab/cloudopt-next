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

import net.cloudopt.next.core.Classer
import net.cloudopt.next.core.ConfigManager
import net.cloudopt.next.core.Plugin
import net.cloudopt.next.jdbc.JDBCConfig
import net.cloudopt.next.jooq.JooqManager.pool
import net.cloudopt.next.jdbc.JDBCConnectionPool
import net.cloudopt.next.jdbc.provider.HikariConnectionPoolProvider
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultTransactionProvider
import java.sql.SQLException
import kotlin.reflect.full.createInstance

class JooqPlugin : Plugin {

    override fun start(): Boolean {

        System.getProperties().setProperty("org.jooq.no-logo", "true")

        try {
            val map = ConfigManager.init("jooq")

            pool = if (map["pool"] != null) {
                Classer.loadClass(map["pool"] as String).createInstance() as JDBCConnectionPool
            } else {
                HikariConnectionPoolProvider(JDBCConfig())
            }

            val sqlDialect = when (map["database"]) {
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
            JooqManager.connection = pool.getConnection()
            JooqManager.connectionProvider = DataSourceConnectionProvider(pool.getDatasource())
            JooqManager.transactionProvider = DefaultTransactionProvider(JooqManager.connectionProvider)
            JooqManager.configuration.set(JooqManager.connectionProvider)
                .set(JooqManager.transactionProvider)
                .set(sqlDialect)
                .set(JooqManager.settings)
            JooqManager.dsl = DSL.using(JooqManager.configuration)
            return true
        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }

    }

    override fun stop(): Boolean {
        return try {
            JooqManager.connection.close()
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }

    }
}