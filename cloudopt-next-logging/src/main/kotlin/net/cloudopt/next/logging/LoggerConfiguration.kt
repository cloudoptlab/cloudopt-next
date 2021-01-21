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

import net.cloudopt.next.logging.provider.LoggerProvider
import net.cloudopt.next.logging.provider.Slf4jLoggerProvider


/*
 * @author: Cloudopt
 * @Time: 2018/10/18
 * @Description: Configuration file
 */

data class LoggerConfiguration(
    var color: Boolean = true,
    var loggerProvider: LoggerProvider = Slf4jLoggerProvider(),
    var debugPrefix: String = "\uD83E\uDD1F DEBUG:",
    var infoPrefix: String = "ℹ️ INFO:",
    var warnPrefix: String = "⚠️ WARN:",
    var errorPrefix: String = "❌ ERRROR:"
)