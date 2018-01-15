package net.cloudopt.next.web.handler

import net.cloudopt.next.logging.Logger
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.web.json.Jsoner
import java.text.SimpleDateFormat
import java.util.Date

/*
 * @author: Cloudopt
 * @Time: 2018/1/15
 * @Description: Used to output route related information
 */
@AutoHandler
class ShowRouteHandler : Handler() {

    override fun handle() {
        if (ConfigManager.webConfig.showRoute){
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            logger.info("Match route ----------------- " + df.format(Date())
                    + " ------------------------------")
            logger.info("Method       : " + request.method())
            logger.info("Path         : " + request.uri())
            logger.info("User-Agent   : " + request.getHeader("User-Agent"))
            logger.info("Params       : " + Jsoner.toJsonString(request.params().entries()))
            logger.info("Cookie       : " + Jsoner.toJsonString(request.getHeader("Cookie")))
            logger.info(
                    "--------------------------------------------------------------------------------")
        }
        next()
    }

    companion object {
        private val logger = Logger.getLogger(ShowRouteHandler::class.java)
    }
}
