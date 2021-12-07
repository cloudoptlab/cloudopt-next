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

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AesEncrypt(password: String) : Encrypt() {

    private val algorithm = "AES"

    private val transformation = "AES/ECB/PKCS7Padding"

    private var key: ByteArray = password.toByteArray()

    init {
        checkBouncyCastleProvider()
    }

    /**
     * Perform AES encryption
     * @param value This is a string that needs to be encrypted
     * @return Encrypted string
     */
    override fun encrypt(value: String): String {
        val encoder = Cipher.getInstance(transformation, "BC")
        val secretKeySpec = SecretKeySpec(key, algorithm)
        encoder.init(1, secretKeySpec)
        val result = encoder.doFinal(value.toByteArray())
        return Base64Encrypt().encrypt(result)
    }

    /**
     * Perform AES decryption
     * @param value This is the need to decrypt the string
     * @return Decrypted string
     */
    override fun decrypt(value: String): String {
        val bytes = Base64Encrypt().decryptToByteArray(value)
        val encoder = Cipher.getInstance(transformation, "BC")
        val secretKeySpec = SecretKeySpec(key, algorithm)
        encoder.init(2, secretKeySpec)
        val decoded = encoder.doFinal(bytes)
        return String(decoded)
    }
}
