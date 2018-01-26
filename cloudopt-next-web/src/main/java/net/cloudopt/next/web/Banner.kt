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
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.yaml.Yamler
import java.io.File
import java.util.*


/*
 * @author: Cloudopt
 * @Time: 2018/1/18
 * @Description: Banner Tool
 */
object Banner {

    private val logger = Logger.getLogger(CloudoptServer.javaClass)

    fun print() {

        if (!ConfigManager.webConfig.banner) {
            return
        }

        var file = File(Yamler.getRootClassPath() + "/banner.txt")

        if (!file.exists()) {
            val fileURL = this.javaClass.getResource("/banner.txt")
            file = File(fileURL.file)
        }
        val sc = Scanner(file)
        var text = ""
        while (sc.hasNextLine()) {
            text = sc.nextLine()
            text = text.replace("\${java.version}", System.getProperty("java.version"))
            text = text.replace("\${java.vendor}", System.getProperty("java.vendor"))
            text = text.replace("\${os}", System.getProperty("os.name"))
            text = text.replace("\${time}", Date().toString())
            logger.info(text)
        }

    }

}