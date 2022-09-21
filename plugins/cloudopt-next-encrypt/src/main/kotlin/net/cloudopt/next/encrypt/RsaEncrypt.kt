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
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


class RsaEncrypt(var publicKeyString: String = "", var privateKeyString: String = "") : Encrypt() {

    private val algorithm = "RSA"

    private lateinit var publicKey: PublicKey

    private lateinit var privateKey: PrivateKey

    private val base64Encrypt: Base64Encrypt = Base64Encrypt()

    init {
        checkBouncyCastleProvider()
        if (publicKeyString.isNotBlank() && privateKeyString.isNotBlank()) {
            publicKey = getPublicKey()
            privateKey = getPrivateKey()
        }
    }

    /**
     * RSA encryption
     * @param value This is a string that needs to be encrypted
     * @return This is an encrypted string
     */
    override fun encrypt(value: String): String {
        return encrypt(value.toByteArray())
    }

    override fun encrypt(value: ByteArray): String {
        val cipher = Cipher.getInstance(algorithm, "BC")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return base64Encrypt.encrypt(cipher.doFinal(value))
    }

    /**
     * RSA decrypt
     * @param value This is a string that needs to be decrypted
     * @return This is the decrypted string
     */
    override fun decrypt(value: String): String {
       return decrypt(base64Encrypt.decryptToByteArray(value))
    }

    override fun decrypt(value: ByteArray): String {
        val cipher = Cipher.getInstance(algorithm, "BC")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val b = cipher.doFinal(value)
        return String(b)
    }

    /**
     * Get the public key
     */
    private fun getPublicKey(): PublicKey {
        val keyBytes: ByteArray = base64Encrypt.decryptToByteArray(publicKeyString)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(algorithm, "BC")
        return keyFactory.generatePublic(keySpec)
    }

    /**
     * Get the private key
     */
    private fun getPrivateKey(): PrivateKey {
        val keyBytes: ByteArray = base64Encrypt.decryptToByteArray(privateKeyString)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(algorithm, "BC")
        return keyFactory.generatePrivate(keySpec)
    }

    /**
     * Generate RSA public key and key
     * @param keySize the keySize.
     * This is an algorithm-specific metric, such as modulus length, specified in number of bits.
     */
    fun generate(keySize: Int = 1024) {
        val keyPairGen = KeyPairGenerator.getInstance(algorithm, "BC")
        keyPairGen.initialize(keySize)
        val keyPair = keyPairGen.generateKeyPair()
        publicKey = keyPair.public
        publicKeyString = base64Encrypt.encrypt(keyPair.public.encoded)
        privateKey = keyPair.private
        privateKeyString = base64Encrypt.encrypt(keyPair.private.encoded)
    }

}
