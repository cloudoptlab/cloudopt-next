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

import java.util.*


/*
 * @author: Cloudopt
 * @Time: 2018/1/8
 * @Description: For Base64 encryption and decryption
 */
class Base64Encrypt : Encrypt() {

    /**
     * Base64 encryption
     * @param value This is a string that needs to be encrypted
     * @return Encrypted string
     */
    override fun encrypt(value: String): String {
        return Base64.getEncoder().encodeToString(value.toByteArray(Charsets.UTF_8))
    }

    /**
     * Base64 encryption
     * @param value This is an array of bytes that need to be encrypted
     * @return Encrypted string
     */
    fun encrypt(value: ByteArray): String {
        return String(Base64.getEncoder().encode(value))
    }

    /**
     * Base64 decrypt
     * @param value This is the need to decrypt the string
     * @return Decrypted string
     */
    override fun decrypt(value: String): String {
        return String(Base64.getDecoder().decode(value))
    }

    /**
     * Base64 decrypt
     * @param value This is the need to decrypt the string
     * @return Decrypted Byte array
     */
    fun decryptToByteArray(value: String): ByteArray {
        return Base64.getDecoder().decode(value)
    }

}
