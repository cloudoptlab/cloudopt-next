/*
 * Copyright 2017-2021 Cloudopt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
class DesEncrypt : Encrypt() {

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