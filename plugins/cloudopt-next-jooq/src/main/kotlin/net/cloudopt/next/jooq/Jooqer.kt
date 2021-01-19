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

import org.jooq.ConnectionProvider
import org.jooq.DSLContext
import org.jooq.TransactionProvider
import org.jooq.conf.Settings
import org.jooq.conf.SettingsTools
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import java.sql.Connection


/*
 * @author: Cloudopt
 * @Time: 2018/2/6
 * @Description: Connection pool manager
 */
object Jooqer {

    @JvmStatic
    var connection: Connection? = null

    @JvmStatic
    var dsl: DSLContext? = null

    @JvmStatic
    var transactionProvider: TransactionProvider? = null

    @JvmStatic
    var connectionProvider: ConnectionProvider? = null

    @JvmStatic
    var settings: Settings = SettingsTools.defaultSettings()

    @JvmStatic
    var configuration = DefaultConfiguration()

    @JvmStatic
    fun refresh() {
        this.dsl = DSL.using(connection)
    }

}