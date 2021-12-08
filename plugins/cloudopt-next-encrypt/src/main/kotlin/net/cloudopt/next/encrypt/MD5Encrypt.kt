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

import java.security.MessageDigest

class MD5Encrypt : Encrypt() {

    private val ALGORITHM = "MD5"

    init {
        checkBouncyCastleProvider()
    }

    /**
     * MD5 encryption
     * @param value This is a string that needs to be encrypted
     * @return Encrypted string
     */
    override fun encrypt(value: String): String {
        return encrypt(value.toByteArray())
    }

    override fun encrypt(value: ByteArray): String {
        val instance: MessageDigest = MessageDigest.getInstance(ALGORITHM, "BC")
        val digest: ByteArray = instance.digest(value)
        return toHexString(digest)
    }

    /**
     * MD5 does not support decryption
     */
    override fun decrypt(value: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decrypt(value: ByteArray): String {
        TODO("Not yet implemented")
    }


}
