package net.cloudopt.next.encrypt

import org.junit.Test

class TestCase {

    @Test
    fun testBase64() {
        val e = Base64Encrypt()
        val s = e.encrypt("hello")
        assert(e.decrypt(s) == "hello")
    }

    @Test
    fun testMD5() {
        val e = MD5Encrypt()
        val s = e.encrypt("hello")
        assert(s.length == 32)
    }

    @Test
    fun testAES() {
        var e = AesEncrypt("lKY7YO6jqRnzNOJ3")
        var s = e.encrypt("hello")
        assert(e.decrypt(s) == "hello")

        e = AesEncrypt("lKY7YO6jqRnzNOJ3", "cloudoptcloudopt")
        s = e.encrypt("hello")
        assert(e.decrypt(s) == "hello")
    }


    @Test
    fun testRSA() {
        var e = RsaEncrypt()
        e.generate()
        var s = e.encrypt("hello")
        assert(e.decrypt(s) == "hello")

        e.generate(2048)
        s = e.encrypt("hello")
        assert(e.decrypt(s) == "hello")

        val publicKeyString = e.publicKeyString
        val privateKeyString = e.privateKeyString
        e = RsaEncrypt(publicKeyString, privateKeyString)
        s = e.encrypt("hello")
        assert(e.decrypt(s) == "hello")
    }

    @Test
    fun testDes() {
        val e = DesEncrypt("12345678")
        val s = e.encrypt("hello")
        assert(e.decrypt(s) == "hello")
    }

    @Test
    fun testSha1() {
        val e = SHA1Encrypt()
        val s = e.encrypt("hello")
        assert(s.length == 40)
    }

    @Test
    fun testSha() {
        val e = SHAEncrypt()
        val s = e.encrypt("hello")
        assert(s.length == 40)
    }

    @Test
    fun testSha256() {
        val e = SHA256Encrypt()
        val s = e.encrypt("hello")
        assert(s.length == 64)
    }

    @Test
    fun testSM2() {
        var e = SM2Encrypt()
        e.generate()
        println(e.privateKeyString)
        var s = e.encrypt("hello")
        println(s)
        assert(e.decrypt(s) == "hello")

        e = SM2Encrypt(
            "033e5250957110884657948ce04507833731e7f88a18569f57f16b7e2abe3a79ad",
            "dbd874c60c8c7432052c7f881ae23f5939d841b12a105bb38ab97129ed02f92d"
        )
        s = e.encrypt("hello")
        assert(e.decrypt(s) == "hello")
    }

    @Test
    fun testSM3() {
        val e: Encrypt = SM3Encrypt()
        assert(e.encrypt("hello") == e.encrypt("hello"))
        assert(e.encrypt("hello") != e.encrypt("hi"))
    }

    @Test
    fun testSM4() {
        var e: Encrypt = SM4Encrypt("lKY7YO6jqRnzNOJ3")
        var s = e.encrypt("hello")
        assert(e.decrypt(s) == "hello")

        e = SM4Encrypt("lKY7YO6jqRnzNOJ3", "cloudoptcloudopt")
        s = e.encrypt("hello")
        assert(e.decrypt(s) == "hello")
    }
}
