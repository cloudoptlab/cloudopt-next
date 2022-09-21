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
package net.cloudopt.next.exposed

import net.cloudopt.next.core.Plugin
import net.cloudopt.next.jdbc.JDBCConnectionManager
import org.jetbrains.exposed.sql.Database


class ExposedPlugin : Plugin {

    override fun start(): Boolean {
        JDBCConnectionManager.dataSourceMap.forEach { map ->
            ExposedManager.databases[map.key] = Database.connect(map.value)
        }
        return true

    }

    override fun stop(): Boolean {
        ExposedManager.databases.forEach { map->
            map.value.connector().close()
        }
        return true
    }
}