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

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/*
 * @author: Cloudopt
 * @Time: 2018/1/8
 * @Description: For AES encryption
 */
class AesEncrypt:Encrypt(){

    private val ALGORITHM = "AES"

    private val TRANSFORMATION = "AES/ECB/PKCS7Padding"

    private var initialized = false

    private var key: ByteArray = ByteArray(10)

    private var password = ""

    /**
     * Set password
     * @param password password
     * @return AesEncrypt object
     */
    fun setPassword(password: String): AesEncrypt {
        this.password = password
        return this
    }

    /**
     * Perform AES encryption
     * @param value This is a string that needs to be encrypted
     * @return Encrypted string
     */
    override fun encrypt(value: String): String {
        key = password.toByteArray()
        initialize()
        var result: ByteArray = ByteArray(10)

        try {
            var encoder = Cipher.getInstance(TRANSFORMATION, "BC")
            var result2 = SecretKeySpec(key, ALGORITHM)
            encoder.init(1, result2)
            result = encoder.doFinal(value.toByteArray())
        } catch (var4: Exception) {
            var4.printStackTrace()
        }

        return Base64Encrypt().encrypt(result)
    }

    /**
     * Perform AES decryption
     * @param value This is the need to decrypt the string
     * @return Decrypted string
     */
    override fun decrypt(value: String): String {
        key = password.toByteArray()
        var bytes = Base64Encrypt().decryptToByteArray(value)
        initialize()
        var result: String? = ""
        try {
            var e = Cipher.getInstance(TRANSFORMATION, "BC")
            var keySpec = SecretKeySpec(key, ALGORITHM)
            e.init(2, keySpec)
            var decoded = e.doFinal(bytes)
            result = String(decoded)
        } catch (var7: Exception) {
            var7.printStackTrace()
        }

        return result!!
    }


    /**
     * Initialization
     */
    private fun initialize(){
        if (!initialized) {
            Security.addProvider(BouncyCastleProvider())
            initialized = true
        }
    }
}
