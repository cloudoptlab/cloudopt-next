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
package net.cloudopt.next.logging.provider

import net.cloudopt.next.logging.Colorer
import net.cloudopt.next.logging.Logger

/*
 * @author: Cloudopt
 * @Time: 2018/1/4
 * @Description: SLF4J log implementation class
 */
class Slf4jLoggerProvider : LoggerProvider {

    override fun getLogger(clazz: Class<*>): Logger {
        return Slf4JLogger(org.slf4j.LoggerFactory.getLogger(clazz))
    }

    override fun getLogger(clazzName: String): Logger {
        return Slf4JLogger(org.slf4j.LoggerFactory.getLogger(clazzName))
    }


    inner class Slf4JLogger internal constructor(private val logger: org.slf4j.Logger) : Logger() {

        override fun debug(message: String) {
            logger.debug(Colorer.cyan(message))
        }

        override fun debug(message: String, vararg args: Any) {
            logger.debug(Colorer.cyan(String.format(message, *args)),*args)
        }

        override fun debug(message: String, t: Throwable) {
            logger.debug(Colorer.cyan(message), t)
        }

        override fun debug(message: String, t: Throwable, vararg args: Any) {
            logger.debug(Colorer.cyan(String.format(message, *args)), t)
        }

        override fun info(message: String) {
            logger.info(Colorer.blue(message))
        }

        override fun info(message: String, vararg args: Any) {
            logger.info(Colorer.blue(String.format(message, *args)),*args)
        }

        override fun info(message: String, t: Throwable) {
            logger.info(Colorer.blue(message), t)
        }

        override fun info(message: String, t: Throwable, vararg args: Any) {
            logger.info(Colorer.blue(String.format(message, *args)), t)
        }

        override fun warn(message: String) {
            logger.warn(Colorer.yellow(message))
        }

        override fun warn(message: String, vararg args: Any) {
            logger.warn(Colorer.yellow(String.format(message, *args)),*args)
        }

        override fun warn(message: String, t: Throwable) {
            logger.warn(Colorer.yellow(message), t)
        }

        override fun warn(message: String, t: Throwable, vararg args: Any) {
            logger.warn(Colorer.yellow(String.format(message, *args)), t)
        }

        override fun error(message: String) {
            logger.error(Colorer.red(message))
        }

        override fun error(message: String, vararg args: Any) {
            logger.error(Colorer.red(String.format(message, *args)),*args)
        }

        override fun error(message: String, t: Throwable) {
            logger.error(Colorer.red(String.format(message)), t)
        }

        override fun error(message: String, t: Throwable, vararg args: Any) {
            logger.error(Colorer.red(String.format(message, *args)), t)
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
