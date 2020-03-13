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

import net.cloudopt.next.logging.Colorer
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.logging.LoggerProvider

import java.util.logging.Level

/*
 * @author: Cloudopt
 * @Time: 2018/1/4
 * @Description: JDK log implementation class
 */

class JdkLoggerProvider : LoggerProvider {

    override fun getLogger(clazz: Class<*>): Logger {
        return JdkLogger(java.util.logging.Logger.getLogger(clazz.name))
    }

    override fun getLogger(clazzName: String): Logger {
        return JdkLogger(java.util.logging.Logger.getLogger(clazzName))
    }


    inner class JdkLogger internal constructor(private val logger: java.util.logging.Logger) : Logger() {
        private val clazzName: String? = null


        @JvmOverloads
        override fun debug(message: String, vararg args: Any) {
            logger.logp(
                Level.FINE,
                clazzName,
                Thread.currentThread().stackTrace[1].methodName,
                "${Colorer.magenta(Logger.configuration.debugPrefix)} ${String.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun debug(message: String, t: Throwable, vararg args: Any) {
            logger.logp(
                Level.FINE,
                clazzName,
                t.stackTrace[1].methodName,
                "${Colorer.magenta(Logger.configuration.debugPrefix)} ${String.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun info(message: String, vararg args: Any) {
            logger.logp(
                Level.INFO,
                clazzName,
                Thread.currentThread().stackTrace[1].methodName,
                "${Colorer.magenta(Logger.configuration.debugPrefix)} ${String.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun info(message: String, t: Throwable, vararg args: Any) {
            logger.logp(
                Level.INFO,
                clazzName,
                t.stackTrace[1].methodName,
                "${Colorer.magenta(Logger.configuration.debugPrefix)} ${String.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun warn(message: String, vararg args: Any) {
            logger.logp(
                Level.WARNING,
                clazzName,
                Thread.currentThread().stackTrace[1].methodName,
                "${Colorer.magenta(Logger.configuration.debugPrefix)} ${String.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun warn(message: String, t: Throwable, vararg args: Any) {
            logger.logp(
                Level.WARNING,
                clazzName,
                t.stackTrace[1].methodName,
                "${Colorer.magenta(Logger.configuration.debugPrefix)} ${String.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun error(message: String, vararg args: Any) {
            logger.logp(
                Level.SEVERE,
                clazzName,
                Thread.currentThread().stackTrace[1].methodName,
                "${Colorer.magenta(Logger.configuration.debugPrefix)} ${String.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun error(message: String, t: Throwable, vararg args: Any) {
            logger.logp(
                Level.SEVERE,
                clazzName,
                t.stackTrace[1].methodName,
                "${Colorer.magenta(Logger.configuration.debugPrefix)} ${String.format(message, *args)}"
            )
        }

        override fun isDebugEnabled(): Boolean {
            return logger.isLoggable(Level.FINE)
        }

        override fun isInfoEnabled(): Boolean {
            return logger.isLoggable(Level.INFO)
        }

        override fun isWarnEnabled(): Boolean {
            return logger.isLoggable(Level.WARNING)
        }

        override fun isErrorEnabled(): Boolean {
            return logger.isLoggable(Level.SEVERE)
        }

    }

}
