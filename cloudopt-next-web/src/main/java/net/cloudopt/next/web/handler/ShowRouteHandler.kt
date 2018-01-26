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
package net.cloudopt.next.web.handler

import net.cloudopt.next.logging.Logger
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.web.json.Jsoner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Map

/*
 * @author: Cloudopt
 * @Time: 2018/1/15
 * @Description: Used to output route related information
 */
@AutoHandler
class ShowRouteHandler : Handler() {

    override fun handle() {
        if (ConfigManager.webConfig.showRoute) {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            logger.info("Match route ----------------- " + df.format(Date())
                    + " ------------------------------")
            logger.info("Method       : " + request?.method())
            logger.info("Path         : " + request?.uri())
            logger.info("User-Agent   : " + request?.getHeader("User-Agent"))
            logger.info("Params       : " + Jsoner.toJsonString(request?.params()?.entries() ?: listOf<Map.Entry<String, String>>()))
            logger.info("Cookie       : " + Jsoner.toJsonString(request.getHeader("Cookie") ?: ""))
            logger.info(
                    "--------------------------------------------------------------------------------")
        }
        next()
    }

    companion object {
        private val logger = Logger.getLogger(ShowRouteHandler::class.java)
    }
}
