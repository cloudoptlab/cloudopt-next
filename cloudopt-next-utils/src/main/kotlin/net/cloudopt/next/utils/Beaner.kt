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
package net.cloudopt.next.utils

/*
 * @author: Cloudopt
 * @Time: 2018/1/4
 * @Description: This is for instantiating objects
 */
object Beaner {
    /**
     * instantiate objects
     * @param clazz class
     * @return objects
     */
    @Throws(InstantiationException::class, IllegalAccessException::class)
    fun <T> newInstance(clazz: Class<*>): T {
        return clazz.newInstance() as T
    }
}
