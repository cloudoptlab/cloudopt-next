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