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


        override fun debug(message: String) {
            logger.logp(Level.FINE, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.cyan(message))
        }

        override fun debug(message: String, t: Throwable) {
            logger.logp(Level.FINE, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.cyan(message), t)
        }

        override fun debug(message: String, vararg args: Any) {
            logger.logp(Level.FINE, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.cyan(String.format(message, *args)))
        }

        override fun debug(message: String, t: Throwable, vararg args: Any) {
            logger.logp(Level.FINE, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.cyan(String.format(message, *args)), t)
        }

        override fun info(message: String) {
            logger.logp(Level.INFO, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.blue(message))
        }

        override fun info(message: String, t: Throwable) {
            logger.logp(Level.INFO, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.blue(message), t)
        }

        override fun info(message: String, vararg args: Any) {
            logger.logp(Level.INFO, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.blue(String.format(message, *args)))
        }

        override fun info(message: String, t: Throwable, vararg args: Any) {
            logger.logp(Level.INFO, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.blue(String.format(message, *args)), t)
        }

        override fun warn(message: String) {
            logger.logp(Level.WARNING, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.yellow(message))
        }

        override fun warn(message: String, t: Throwable) {
            logger.logp(Level.WARNING, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.yellow(message), t)
        }

        override fun warn(message: String, vararg args: Any) {
            logger.logp(Level.WARNING, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.yellow(String.format(message, *args)))
        }

        override fun warn(message: String, t: Throwable, vararg args: Any) {
            logger.logp(Level.WARNING, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.yellow(String.format(message, *args)), t)
        }

        override fun error(message: String) {
            logger.logp(Level.SEVERE, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.red(message))
        }

        override fun error(message: String, t: Throwable) {
            logger.logp(Level.SEVERE, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.red(message), t)
        }

        override fun error(message: String, vararg args: Any) {
            logger.logp(Level.SEVERE, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.red(String.format(message, *args)))
        }

        override fun error(message: String, t: Throwable, vararg args: Any) {
            logger.logp(Level.SEVERE, clazzName, Thread.currentThread().stackTrace[1].methodName, Colorer.red(String.format(message, *args)), t)
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
