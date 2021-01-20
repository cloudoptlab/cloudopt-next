/*
 * Copyright 2017 Cloudopt.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Apache License v2.0 which accompanies this distribution.
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package net.cloudopt.next.logging.provider

import net.cloudopt.next.logging.Colorer
import net.cloudopt.next.logging.Format
import net.cloudopt.next.logging.Logger
import kotlin.reflect.KClass


/*
 * @author: Cloudopt
 * @Time: 2018/10/18
 * @Description: SLF4J log implementation class
 */
class Slf4jLoggerProvider : LoggerProvider {

    private val format = Format("{", "}")

    override fun getLogger(clazz: KClass<*>): Logger {
        return Slf4JLogger(org.slf4j.LoggerFactory.getLogger(clazz.java))
    }

    override fun getLogger(clazzName: String): Logger {
        return Slf4JLogger(org.slf4j.LoggerFactory.getLogger(clazzName))
    }


    inner class Slf4JLogger internal constructor(private val logger: org.slf4j.Logger) : Logger() {

        override fun debug(message: String, vararg args: Any) {
            logger.debug("${Colorer.magenta(Logger.configuration.debugPrefix)} ${format.format(message, *args)}", *args)
        }

        override fun debug(message: String, t: Throwable, vararg args: Any) {
            logger.debug("${Colorer.green(Logger.configuration.debugPrefix)} ${format.format(message, *args)}", t)
        }

        override fun info(message: String, vararg args: Any) {
            logger.info("${Colorer.blue(Logger.configuration.infoPrefix)} ${format.format(message, *args)}", *args)
        }

        override fun info(message: String, t: Throwable, vararg args: Any) {
            logger.info("${Colorer.blue(Logger.configuration.infoPrefix)} ${format.format(message, *args)}", t)
        }

        override fun warn(message: String, vararg args: Any) {
            logger.warn("${Colorer.yellow(Logger.configuration.warnPrefix)} ${format.format(message, *args)}", *args)
        }

        override fun warn(message: String, t: Throwable, vararg args: Any) {
            logger.warn("${Colorer.yellow(Logger.configuration.warnPrefix)} ${format.format(message, *args)}", t)
        }

        override fun error(message: String, vararg args: Any) {
            logger.error("${Colorer.red(Logger.configuration.errorPrefix)} ${format.format(message, *args)}", *args)
        }

        override fun error(message: String, t: Throwable, vararg args: Any) {
            logger.error("${Colorer.red(Logger.configuration.errorPrefix)} ${format.format(message, *args)}", t)
        }

        override fun isDebugEnabled(): Boolean {
            return logger.isDebugEnabled
        }

        override fun isInfoEnabled(): Boolean {
            return logger.isInfoEnabled
        }

        override fun isWarnEnabled(): Boolean {
            return logger.isWarnEnabled
        }

        override fun isErrorEnabled(): Boolean {
            return logger.isErrorEnabled
        }
    }
}
