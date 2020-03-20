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
package net.cloudopt.next.web

import net.cloudopt.next.logging.Logger
import java.io.BufferedReader
import java.io.InputStreamReader


/*
 * @author: Cloudopt
 * @Time: 2020/3/13
 * @Description: Welcome Tool
 */
object Welcomer {

    private val logger = Logger.getLogger(CloudoptServer.javaClass)

    @JvmStatic
    fun html(): String {

        var input = Welcomer.javaClass.getClassLoader().getResourceAsStream("welcome.html")

        var buffer = BufferedReader(InputStreamReader(input))

        var line = buffer.readLine()

        var welcomeHtml = ""

        while (line != null) {
            welcomeHtml = welcomeHtml + line
            line = buffer.readLine()
        }

        buffer.close()

        return welcomeHtml

    }

}