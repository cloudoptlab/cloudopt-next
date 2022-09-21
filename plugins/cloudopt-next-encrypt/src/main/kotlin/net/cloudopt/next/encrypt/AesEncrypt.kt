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
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AesEncrypt(password: String, iv: String = "") : Encrypt() {

    private val algorithm = "AES"

    private val transformation = if (iv.isBlank()) {
        "AES/ECB/PKCS7Padding"
    } else {
        "AES/CBC/PKCS7Padding"
    }

    private lateinit var ivParameterSpec: IvParameterSpec

    private var key: ByteArray = password.toByteArray()

    init {
        checkBouncyCastleProvider()
        if (iv.isNotBlank()) {
            ivParameterSpec = IvParameterSpec(iv.toByteArray())
        }
    }

    /**
     * Perform AES encryption
     * @param value This is a string that needs to be encrypted
     * @return Encrypted string
     */
    override fun encrypt(value: String): String {
        return encrypt(value.toByteArray())
    }

    override fun encrypt(value: ByteArray): String {
        val encoder = Cipher.getInstance(transformation, "BC")
        val secretKeySpec = SecretKeySpec(key, algorithm)
        if (transformation == "AES/ECB/PKCS7Padding") {
            encoder.init(1, secretKeySpec)
        } else {
            encoder.init(1, secretKeySpec, ivParameterSpec)
        }
        val result = encoder.doFinal(value)
        return Base64Encrypt().encrypt(result)
    }

    /**
     * Perform AES decryption
     * @param value This is the need to decrypt the string
     * @return Decrypted string
     */
    override fun decrypt(value: String): String {
        return decrypt(Base64Encrypt().decryptToByteArray(value))
    }

    override fun decrypt(value: ByteArray): String {
        val encoder = Cipher.getInstance(transformation, "BC")
        val secretKeySpec = SecretKeySpec(key, algorithm)
        if (transformation == "AES/ECB/PKCS7Padding") {
            encoder.init(2, secretKeySpec)
        } else {
            encoder.init(2, secretKeySpec, ivParameterSpec)
        }
        val decoded = encoder.doFinal(value)
        return String(decoded)
    }
}
