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
package net.cloudopt.next.clickhouse.test

import net.cloudopt.next.clickhouse.ClickHouseManager
import net.cloudopt.next.clickhouse.ClickHousePlugin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class TestCase {

    val plugin = ClickHousePlugin()

    @BeforeTest
    fun testStart() {
        plugin.start()
    }

    @AfterTest
    fun testStop() {
        plugin.stop()
    }

    @Test
    fun testShowDataBase() {
        val statement = ClickHouseManager.hikariDataSource.connection.createStatement()
        val result = statement.executeQuery("show databases")
        assert(result != null)
    }

}