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

import net.cloudopt.next.logging.Logger

/*
 * @author: Cloudopt
 * @Time: 2018/10/18
 * @Description: Log interface
 */
interface LoggerProvider {

    fun getLogger(clazz: Class<*>): Logger

    fun getLogger(clazzName: String): Logger


}
