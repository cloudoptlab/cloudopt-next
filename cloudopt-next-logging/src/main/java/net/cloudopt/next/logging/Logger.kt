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
package net.cloudopt.next.logging

/*
 * @author: Cloudopt
 * @Time: 2018/10/18
 * @Description: Abstract class for log output
 */
abstract class Logger {

    abstract fun isDebugEnabled(): Boolean

    abstract fun isInfoEnabled(): Boolean

    abstract fun isWarnEnabled(): Boolean

    abstract fun isErrorEnabled(): Boolean

    abstract fun debug(format: String, vararg args: Any)

    abstract fun debug(format: String, t: Throwable, vararg args: Any)

    abstract fun info(format: String, vararg args: Any)

    abstract fun info(format: String, t: Throwable, vararg args: Any)

    abstract fun warn(format: String, vararg args: Any)

    abstract fun warn(format: String, t: Throwable, vararg args: Any)

    abstract fun error(format: String, vararg args: Any)

    abstract fun error(format: String, t: Throwable, vararg args: Any)


    companion object {

        @JvmStatic
        open var configuration = LoggerConfiguration()

        fun getLogger(clazz: Class<*>): Logger {
            return configuration.loggerProvider.getLogger(clazz)
        }

        fun getLogger(clazzName: String): Logger {
            return configuration.loggerProvider.getLogger(clazzName)
        }
    }
}
