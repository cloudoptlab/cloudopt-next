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


package net.cloudopt.next.redis

/**
 * @author: Cloudopt
 * @Time: 2018/2/8
 * @Description: IKeyNamingPolicy.
 * Architects can implement this type of global Key naming strategy，
 * For example, Integer, String, OtherType these different types of objects
 * Choose a different naming method, the default naming method is Object.toString ()
 */
interface IKeyNamingPolicy {

    fun getKeyName(key: Any): String

    companion object {

        val defaultKeyNamingPolicy: IKeyNamingPolicy = object : IKeyNamingPolicy {
            override fun getKeyName(key: Any): String {
                return key.toString()
            }
        }
    }
}




