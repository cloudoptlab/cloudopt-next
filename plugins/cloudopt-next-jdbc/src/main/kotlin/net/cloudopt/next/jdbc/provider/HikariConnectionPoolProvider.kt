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
package net.cloudopt.next.jdbc.provider

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.cloudopt.next.jdbc.JDBCConfig
import net.cloudopt.next.jdbc.JDBCConnectionPool
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

class HikariConnectionPoolProvider : JDBCConnectionPool {

    private val config = HikariConfig()

    private lateinit var dataSource: DataSource

    override fun init(jdbcConfig: JDBCConfig) {
        config.jdbcUrl = jdbcConfig.jdbcUrl
        config.username = jdbcConfig.username
        config.password = jdbcConfig.password
        config.driverClassName = jdbcConfig.driverClassName
        dataSource = HikariDataSource(config)
    }

    @Throws(SQLException::class)
    override fun getConnection(): Connection {
        return dataSource.connection
    }

    override fun getDatasource(): DataSource {
        return dataSource
    }


}