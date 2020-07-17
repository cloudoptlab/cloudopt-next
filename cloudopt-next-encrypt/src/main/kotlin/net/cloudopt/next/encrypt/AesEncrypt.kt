/*
 * Copyright 2017-2020 original authors
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

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/*
 * @author: Cloudopt
 * @Time: 2018/1/8
 * @Description: For AES encryption
 */
class AesEncrypt : Encrypt() {

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
    private fun initialize() {
        if (!initialized) {
            Security.addProvider(BouncyCastleProvider())
            initialized = true
        }
    }
}
