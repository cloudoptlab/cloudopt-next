/*
 * Copyright 2017-2020 original authors
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
package net.cloudopt.next.web

import net.cloudopt.next.logging.Logger
import net.cloudopt.next.utils.Resourcer
import net.cloudopt.next.web.config.ConfigManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


/*
 * @author: Cloudopt
 * @Time: 2018/1/18
 * @Description: Banner Tool
 */
object Banner {

    private val logger = Logger.getLogger(NextServer.javaClass)

    @JvmStatic
    fun print() {

        if (!ConfigManager.config.banner) {
            return
        }
        var input = Banner.javaClass.getClassLoader().getResourceAsStream("banner.txt")

        var buffer = BufferedReader(InputStreamReader(input))

        if (ConfigManager.config.bannerName.isNotBlank()) {
            input = Resourcer.getFileInputStream(ConfigManager.config.bannerName)
            buffer = BufferedReader(InputStreamReader(input))
        }

        var text = buffer.readLine()
        while (text != null) {
            text = text.replace("\${java.version}", System.getProperty("java.version"))
            text = text.replace("\${java.vendor}", System.getProperty("java.vendor"))
            text = text.replace("\${os}", System.getProperty("os.name"))
            text =
                text.replace("\${time}", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            text = text.replace("\${port}", ConfigManager.config.port.toString())
            logger.info(text)
            text = buffer.readLine()
        }

        buffer.close()

    }

}