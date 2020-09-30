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

package net.cloudopt.next.logging.provider

import net.cloudopt.next.logging.Colorer
import net.cloudopt.next.logging.Format
import net.cloudopt.next.logging.Logger
import java.util.logging.Level

/*
 * @author: Cloudopt
 * @Time: 2018/1/4
 * @Description: JDK log implementation class
 */

class JdkLoggerProvider : LoggerProvider {

    private val format = Format("{","}")

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
                    "${Colorer.magenta(Logger.configuration.debugPrefix)} ${format.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun debug(message: String, t: Throwable, vararg args: Any) {
            logger.logp(
                    Level.FINE,
                    clazzName,
                    t.stackTrace[1].methodName,
                    "${Colorer.magenta(Logger.configuration.debugPrefix)} ${format.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun info(message: String, vararg args: Any) {
            logger.logp(
                    Level.INFO,
                    clazzName,
                    Thread.currentThread().stackTrace[1].methodName,
                    "${Colorer.magenta(Logger.configuration.debugPrefix)} ${format.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun info(message: String, t: Throwable, vararg args: Any) {
            logger.logp(
                    Level.INFO,
                    clazzName,
                    t.stackTrace[1].methodName,
                    "${Colorer.magenta(Logger.configuration.debugPrefix)} ${format.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun warn(message: String, vararg args: Any) {
            logger.logp(
                    Level.WARNING,
                    clazzName,
                    Thread.currentThread().stackTrace[1].methodName,
                    "${Colorer.magenta(Logger.configuration.debugPrefix)} ${format.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun warn(message: String, t: Throwable, vararg args: Any) {
            logger.logp(
                    Level.WARNING,
                    clazzName,
                    t.stackTrace[1].methodName,
                    "${Colorer.magenta(Logger.configuration.debugPrefix)} ${format.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun error(message: String, vararg args: Any) {
            logger.logp(
                    Level.SEVERE,
                    clazzName,
                    Thread.currentThread().stackTrace[1].methodName,
                    "${Colorer.magenta(Logger.configuration.debugPrefix)} ${format.format(message, *args)}"
            )
        }

        @JvmOverloads
        override fun error(message: String, t: Throwable, vararg args: Any) {
            logger.logp(
                    Level.SEVERE,
                    clazzName,
                    t.stackTrace[1].methodName,
                    "${Colorer.magenta(Logger.configuration.debugPrefix)} ${format.format(message, *args)}"
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
