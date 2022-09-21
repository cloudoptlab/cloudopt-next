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

import java.sql.Connection
import javax.sql.DataSource

interface JDBCConnectionPool {

    /**
     * Used to initialize the thread pool
     * @param jdbcConfig JDBCConfig
     * @see JDBCConfig
     */
    fun init(jdbcConfig: JDBCConfig)

    /**
     * Attempts to establish a connection with the data source that
     * this DataSource object represents.
     * @see DataSource
     * @see Connection
     * @return  a connection to the data source
     */
    fun getConnection(): Connection

    /**
     * Get the datasource object.
     * @see DataSource
     * @return DataSource
     */
    fun getDatasource(): DataSource

}

