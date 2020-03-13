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
package net.cloudopt.next.jooq

import org.jooq.ConnectionProvider
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.conf.Settings
import java.sql.Connection
import org.jooq.TransactionProvider
import org.jooq.conf.SettingsTools
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration


/*
 * @author: Cloudopt
 * @Time: 2018/2/6
 * @Description: Connection pool manager
 */
object Jooqer {

    @JvmStatic
    open var connection: Connection? = null

    @JvmStatic
    open var dsl: DSLContext? = null

    @JvmStatic
    open var transactionProvider: TransactionProvider? = null

    @JvmStatic
    open var connectionProvider:ConnectionProvider ? = null

    @JvmStatic
    open var settings:Settings = SettingsTools.defaultSettings()

    @JvmStatic
    open var configuration =  DefaultConfiguration()

    @JvmStatic
    open fun refresh(){
         this.dsl = DSL.using(connection)
    }

}