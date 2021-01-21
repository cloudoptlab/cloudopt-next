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

import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/*
 * @author: Cloudopt
 * @Time: 2018/1/8
 * @Description: For MD5 encryption
 */
class RsaEncrypt : Encrypt() {

    private val ALGORITHM = "RSA"

    private var publicKey = ""

    private var privateKey = ""

    /**
     * Set the public key
     * @param PublicKey Public key
     * @return RsaEncrypt object
     */
    fun setPublicKey(publicKey: String): RsaEncrypt {
        this.publicKey = publicKey
        return this
    }

    /**
     * Set the private key
     * @param PrivateKey private key
     * @return RsaEncrypt object
     */
    fun setPrivateKey(privateKey: String): RsaEncrypt {
        this.privateKey = privateKey
        return this
    }

    /**
     * RSA encryption
     * @param value This is a string that needs to be encrypted
     * @return This is an encrypted string
     */
    override fun encrypt(value: String): String {
        val key = getPublicKey()
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        var b = value.toByteArray()
        var b1 = cipher.doFinal(b)
        return Base64Encrypt().encrypt(b1)
    }

    /**
     * RSA decrypt
     * @param value This is a string that needs to be decrypted
     * @return This is the decrypted string
     */
    override fun decrypt(value: String): String {
        val key = getPrivateKey()
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        var b1 = Base64Encrypt().decryptToByteArray(value)
        var b = cipher.doFinal(b1)
        return String(b)
    }

    /**
     * Get the public key
     * @param key Key string (base64 encoded)
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getPublicKey(): PublicKey {
        var keyBytes: ByteArray
        keyBytes = Base64Encrypt().decryptToByteArray(publicKey)
        var keySpec = X509EncodedKeySpec(keyBytes)
        var keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }

    /**
     * Get the private key
     * @param key Key string (base64 encoded)
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getPrivateKey(): PrivateKey {
        var keyBytes: ByteArray
        keyBytes = Base64Encrypt().decryptToByteArray(privateKey)
        var keySpec = PKCS8EncodedKeySpec(keyBytes)
        var keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }
}
