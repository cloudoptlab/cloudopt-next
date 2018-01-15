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

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec


/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: For DES encryption (ECB mode)
 */
class DesEncrypt :Encrypt(){

    private val ALGORITHM = "DES"

    private val TRANSFORMATION = "DES/ECB/PKCS5Padding"

    private var password = ""

    private val base64 = Base64Encrypt()

    /**
     * Set password
     * @param password Password
     * @return DesEncrypt object
     */
    fun setPassword(password: String): DesEncrypt {
        this.password = password
        return this
    }

    /**
     * DES encryption
     * @param value This is a string that needs to be encrypted
     * @return Encoded Byte [] Base64 encoded
     */
    override fun encrypt(value: String): String {
        var cipher = Cipher.getInstance(TRANSFORMATION)
        var secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
        var keySpec = DESKeySpec(password.toByteArray())
        var secretKey = secretKeyFactory.generateSecret(keySpec)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, SecureRandom())
        return Base64Encrypt().encrypt(cipher.doFinal(value.toByteArray()))
    }

    /**
     * DES decryption 
     * @param value This is a string that needs to be decrypted
     * @return The encrypted string first Base64 decoding and decryption
     */
    override fun decrypt(value: String): String {
        var v = Base64Encrypt().decryptToByteArray(value)
        var deCipher = Cipher.getInstance(TRANSFORMATION)
        var deDecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
        var deKeySpec = DESKeySpec(password.toByteArray())
        var deSecretKey = deDecretKeyFactory.generateSecret(deKeySpec)
        deCipher.init(Cipher.DECRYPT_MODE, deSecretKey, SecureRandom())
        return String(deCipher.doFinal(v))
    }


}