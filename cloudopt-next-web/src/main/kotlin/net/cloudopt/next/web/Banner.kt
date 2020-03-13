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

import net.cloudopt.next.utils.Resourcer
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.web.config.ConfigManager
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.io.InputStreamReader
import java.io.BufferedReader


/*
 * @author: Cloudopt
 * @Time: 2018/1/18
 * @Description: Banner Tool
 */
object Banner {

    private val logger = Logger.getLogger(CloudoptServer.javaClass)

    @JvmStatic
    fun print() {

        if (!ConfigManager.webConfig.banner) {
            return
        }
        var input = Banner.javaClass.getClassLoader().getResourceAsStream("banner.txt")

        var buffer = BufferedReader(InputStreamReader(input))

        if(ConfigManager.webConfig.bannerName.isNotBlank()){
            input = Resourcer.getFileInputStream(ConfigManager.webConfig.bannerName)
            buffer = BufferedReader(InputStreamReader(input))
        }

        var text = buffer.readLine()
        while (text != null) {
            text = text.replace("\${java.version}", System.getProperty("java.version"))
            text = text.replace("\${java.vendor}", System.getProperty("java.vendor"))
            text = text.replace("\${os}", System.getProperty("os.name"))
            text = text.replace("\${time}", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            text = text.replace("\${port}", ConfigManager.webConfig.port.toString())
            logger.info(text)
            text = buffer.readLine()
        }

        buffer.close()

    }

}