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

import java.util.*

class Base64Encrypt : Encrypt() {

    init {
        checkBouncyCastleProvider()
    }

    /**
     * Base64 encryption
     * @param value This is a string that needs to be encrypted
     * @return Encrypted string
     */
    override fun encrypt(value: String): String {
        return encrypt(value.toByteArray(Charsets.UTF_8))
    }

    /**
     * Base64 encryption
     * @param value This is an array of bytes that need to be encrypted
     * @return Encrypted string
     */
    override fun encrypt(value: ByteArray): String {
        return Base64.getEncoder().encodeToString(value)
    }

    /**
     * Base64 decrypt
     * @param value This is the need to decrypt the string
     * @return Decrypted string
     */
    override fun decrypt(value: String): String {
        return String(Base64.getDecoder().decode(value))
    }

    override fun decrypt(value: ByteArray): String {
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
