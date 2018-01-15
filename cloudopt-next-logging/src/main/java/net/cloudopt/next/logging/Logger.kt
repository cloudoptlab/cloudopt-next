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
package net.cloudopt.next.logging

import net.cloudopt.next.logging.provider.JdkLoggerProvider
import net.cloudopt.next.logging.provider.LoggerProvider
import net.cloudopt.next.logging.provider.Slf4jLoggerProvider

/*
 * @author: Cloudopt
 * @Time: 2018/1/4
 * @Description: Abstract class for log output
 */
abstract class Logger {

    abstract fun isDebugEnabled(): Boolean

    abstract fun isInfoEnabled(): Boolean

    abstract fun isWarnEnabled(): Boolean

    abstract fun isErrorEnabled(): Boolean


    abstract fun debug(message: String)

    abstract fun debug(format: String, vararg args: Any)

    abstract fun debug(message: String, t: Throwable)

    abstract fun debug(format: String, t: Throwable, vararg args: Any)

    abstract fun info(message: String)

    abstract fun info(format: String, vararg args: Any)

    abstract fun info(message: String, t: Throwable)

    abstract fun info(format: String, t: Throwable, vararg args: Any)

    abstract fun warn(message: String)

    abstract fun warn(format: String, vararg args: Any)

    abstract fun warn(message: String, t: Throwable)

    abstract fun warn(format: String, t: Throwable, vararg args: Any)

    abstract fun error(message: String)

    abstract fun error(format: String, vararg args: Any)

    abstract fun error(message: String, t: Throwable)

    abstract fun error(format: String, t: Throwable, vararg args: Any)

    companion object {

        private var loggerProvider: LoggerProvider? = null

        init {
            try {
                Class.forName("org.slf4j.Logger")
                loggerProvider = Slf4jLoggerProvider()
            } catch (ex: ClassNotFoundException) {
                loggerProvider = JdkLoggerProvider()
            }

        }

        fun getLogger(clazz: Class<*>): Logger {
            return loggerProvider!!.getLogger(clazz)
        }

        fun getLogger(clazzName: String): Logger {
            return loggerProvider!!.getLogger(clazzName)
        }
    }
}
