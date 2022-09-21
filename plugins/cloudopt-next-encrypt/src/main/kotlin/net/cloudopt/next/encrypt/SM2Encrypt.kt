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

import org.bouncycastle.asn1.gm.GMNamedCurves
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
import org.bouncycastle.jce.spec.ECParameterSpec
import org.bouncycastle.jce.spec.ECPrivateKeySpec
import org.bouncycastle.jce.spec.ECPublicKeySpec
import org.bouncycastle.util.encoders.Hex
import java.math.BigInteger
import java.security.*
import java.security.spec.ECGenParameterSpec
import javax.crypto.Cipher


class SM2Encrypt(var publicKeyString: String = "", var privateKeyString: String = "") : Encrypt() {

    private val algorithm = "SM2"

    private lateinit var publicKey: BCECPublicKey

    private lateinit var privateKey: BCECPrivateKey

    private val base64Encrypt: Base64Encrypt = Base64Encrypt()

    init {
        checkBouncyCastleProvider()
        if (publicKeyString.isNotBlank() && privateKeyString.isNotBlank()) {
            publicKey = getPublicKey()
            privateKey = getPrivateKey()
        }
    }

    /**
     * SM2 encryption
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
     * SM2 decrypt
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
    private fun getPublicKey(): BCECPublicKey {
        val parameters = GMNamedCurves.getByName("sm2p256v1")
        val ecParameterSpec =
            ECParameterSpec(parameters.getCurve(), parameters.getG(), parameters.getN(), parameters.getH())
        val ecPoint = parameters.getCurve().decodePoint(Hex.decode(publicKeyString))
        val keyFactory = KeyFactory.getInstance("EC", "BC")
        publicKey = keyFactory.generatePublic(ECPublicKeySpec(ecPoint, ecParameterSpec)) as BCECPublicKey
        return publicKey
    }

    /**
     * Get the private key
     */
    private fun getPrivateKey(): BCECPrivateKey {
        val parameters = GMNamedCurves.getByName("sm2p256v1")
        val ecParameterSpec =
            ECParameterSpec(parameters.getCurve(), parameters.getG(), parameters.getN(), parameters.getH())
        val bigInteger = BigInteger(privateKeyString, 16)
        val keyFactory = KeyFactory.getInstance("EC", "BC")
        privateKey = keyFactory.generatePrivate(ECPrivateKeySpec(bigInteger, ecParameterSpec)) as BCECPrivateKey
        return privateKey
    }

    /**
     * Generate SM2 public key and key
     * @param keySize the keySize.
     * This is an algorithm-specific metric, such as modulus length, specified in number of bits.
     */
    fun generate() {
        val genParameterSpec = ECGenParameterSpec("sm2p256v1")
        val keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        keyPairGenerator.initialize(genParameterSpec, SecureRandom())
        val keyPair = keyPairGenerator.generateKeyPair()
        privateKey = keyPair.getPrivate() as BCECPrivateKey
        publicKey = keyPair.getPublic() as BCECPublicKey
        privateKeyString = privateKey.getD().toString(16)
        publicKeyString = String(Hex.encode(publicKey.getQ().getEncoded(true)))
    }

}
