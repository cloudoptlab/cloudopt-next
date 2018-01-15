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
package net.cloudopt.next.encrypt

/*
 * @author: Cloudopt
 * @Time: 2018/1/5
 * @Description: Encryption module common interface
 */
abstract class Encrypt {

    abstract fun encrypt(value: String): String

    abstract fun decrypt(value: String): String

    /**
     * Convert Byte [] to a string
     * @param value The array to be converted
     * @return This is the converted string
     */
    fun toHexString(value:ByteArray): String {
        var sb : StringBuffer = StringBuffer()
        for (b in value) {
            var i :Int = b.toInt() and 0xff
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                hexString = "0" + hexString
            }
            sb.append(hexString)
        }
        return sb.toString()
    }
}
