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
package net.cloudopt.next.web.test

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import net.cloudopt.next.web.CloudoptServer
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.web.event.EventPlugin
import net.cloudopt.next.web.render.FreemarkerRender
import net.cloudopt.next.web.test.plugin.TestPlugin
import net.cloudopt.next.yaml.Yamler
import java.io.File
import kotlin.reflect.KClass
import kotlin.test.fail

/*
 * @author: Cloudopt
 * @Time: 2018/1/17
 * @Description: Test Case
 */
fun main(args: Array<String>) {
    CloudoptServer.addPlugin(TestPlugin())
    CloudoptServer.addPlugin(EventPlugin())
    CloudoptServer.run(TestCase::class.java)
}

class TestCase