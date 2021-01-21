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
package net.cloudopt.next.jooq.pool

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.cloudopt.next.web.config.ConfigManager
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource


/*
 * @author: Cloudopt
 * @Time: 2018/2/6
 * @Description: Hikaricp helper
 */
class HikariCPPool : ConnectionPool {

    private val datasourceConfig: MutableMap<String, Any> = ConfigManager.init("datasource")

    private val config = HikariConfig()

    init {
        config.jdbcUrl = datasourceConfig.get("jdbcUrl") as String
        config.username = datasourceConfig.get("username") as String
        config.password = datasourceConfig.get("password") as String
        config.driverClassName = datasourceConfig.get("driverClassName") as String
        datasourceConfig.keys.forEach { key ->
            config.addDataSourceProperty(key, datasourceConfig.get(key))
        }
    }

    @Throws(SQLException::class)
    override fun getConnection(): Connection {
        return getDatasource().connection
    }

    override fun getDatasource(): DataSource {
        return HikariDataSource(config)
    }


}