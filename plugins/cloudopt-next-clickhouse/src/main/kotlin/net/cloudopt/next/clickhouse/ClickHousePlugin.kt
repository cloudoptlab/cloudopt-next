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
package net.cloudopt.next.clickhouse

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.cloudopt.next.utils.Maper.toProperties
import net.cloudopt.next.web.Plugin
import net.cloudopt.next.web.config.ConfigManager
import ru.yandex.clickhouse.ClickHouseDataSource
import ru.yandex.clickhouse.settings.ClickHouseProperties


class ClickHousePlugin : Plugin {
    override fun start(): Boolean {

        val clickHouseConfigMap = ConfigManager.init("clickhouse")

        val hikariConfigMap = ConfigManager.init("clickhouse.hikari")

        return if (clickHouseConfigMap["jdbcUrl"] != null && clickHouseConfigMap["jdbcUrl"].toString().isNotBlank()) {


            val clickHouseProperties = ClickHouseProperties()
            ClickHouseManager.clickHouseDataSource =
                ClickHouseDataSource(clickHouseConfigMap["jdbcUrl"].toString(), clickHouseProperties)

            try {
                Class.forName("com.zaxxer.hikari.HikariConfig")
                val hikariConfig = HikariConfig(hikariConfigMap.toProperties())
                hikariConfig.dataSource = ClickHouseManager.clickHouseDataSource
                ClickHouseManager.hikariDataSource = HikariDataSource(hikariConfig)
            } catch (e: ClassNotFoundException) {
            }

            true
        } else {
            false
        }
    }

    override fun stop(): Boolean {
        ClickHouseManager.clickHouseDataSource.connection.close()
        ClickHouseManager.hikariDataSource.connection.close()
        return true
    }
}