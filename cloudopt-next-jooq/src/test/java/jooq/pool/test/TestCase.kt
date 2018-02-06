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
package jooq.pool.test

import net.cloudopt.next.jooq.Jooqer
import net.cloudopt.next.jooq.plugin.JooqPlugin
import org.junit.Test


/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Test Case
 */
class TestCase{

    @Test
    fun testConnection(){
        var plugin = JooqPlugin()
        plugin.start()
    }

}