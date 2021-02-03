package net.cloudopt.next.encrypt

import org.junit.Test

/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Test Case
 */
class TestCase {

    @Test
    fun testBase64() {
        var e = Base64Encrypt()
        var s = e.encrypt("hello")
        println(s)
        println(e.decrypt(s))
    }

    @Test
    fun testMD5() {
        var e = MD5Encrypt()
        var s = e.encrypt("hello")
        println(s)
    }

    @Test
    fun testAES() {
        var e = AesEncrypt()
        var s = e.setPassword("lKY7YO6jqRnzNOJ3").encrypt("hello")
        println(s)
        println(e.decrypt(s))
    }

    @Test
    fun testRSA() {
        var publicK =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCFmf/HWpTZ9smPjyM6SUa0UvUQGfIY+OMV5S8zNqmwz11pYovtz57okRZreK8RtBkBOcOKyk7KRMm0agMm0qBaz0ESuFJmIbl3pEn3l/m0aCNnFv2ehijXl6AoW3bB/fKFoKUcRGXet4R/ka1qcxUJaH3uZtmiyPn8G66BhZXBbQIDAQAB"
        var privateK =
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIWZ/8dalNn2yY+PIzpJRrRS9RAZ8hj44xXlLzM2qbDPXWlii+3PnuiRFmt4rxG0GQE5w4rKTspEybRqAybSoFrPQRK4UmYhuXekSfeX+bRoI2cW/Z6GKNeXoChbdsH98oWgpRxEZd63hH+RrWpzFQlofe5m2aLI+fwbroGFlcFtAgMBAAECgYAZMloS9vprwSdyc8RpEbjL+XlOeBY4r3fkgTzNo9mNBw7O+U76otWNdw+LZU9fP2AX4xUF7/G8JA0GgZfmkoK7VTbpBpX7BeW664GzVa9sDFsf2WMD01J9mndcHjcyyeuwBSXliJupjDAxpDTWcd6U2gMgv0SsTxCZQo2Wdr0y4QJBAPP/OY4Mc68P2bwBXyJcnKZYC0sSgeudZ8URXNbNtEgiklO5SV5hteEBfHYjCzbLf7zE38pMNVtuNua2paNM1kcCQQCMLIKO3wRDQgo/LZxy58kjknAVbZJypZj6Yz8O8poIB2sUyyBPNVOXLg4eOSBMQXH2yJNIw4kv+KAxA+8D2GCrAkEAwHV7Ao7T2SxZhLBYSBRhA9zC2653iFAagBlX759GKvgKD7xBIQ9VlWvErrKpr8kIsu9fzoQaOkpPR+Cd+pcrFQJAfrfmNx5pjhvvg3nKSx46+UtyxAxQLhCCISkDYpHyqXt7VErlJHYC4VKjNLNT/VvUmNJuQ4NxS8qplmYF9yXvDQJAO/GlhZ+qmCOpfKb/pOHRUYSZsPZ0/85sQDQ2u/GYp4jtDqeYNoqZt0Oqr9bEJkJ2sRZnoRGXUePuR7kMoejy9w=="
        var e = RsaEncrypt()
        e.setPublicKey(publicK).setPrivateKey(privateK)
        var s = e.encrypt("hello")
        println(s)
        println(e.decrypt(s))
    }

    @Test
    fun testDes() {
        var e = DesEncrypt().setPassword("12345678")
        var s = e.encrypt("hello")
        println(s)
        println(e.decrypt(s))
    }

    @Test
    fun testSha1() {
        var e = SHA1Encrypt()
        var s = e.encrypt("hello")
        println(s)
    }

    @Test
    fun testSha() {
        var e = SHAEncrypt()
        var s = e.encrypt("hello")
        println(s)
    }

    @Test
    fun testSha256() {
        var e = SHA256Encrypt()
        var s = e.encrypt("hello")
        println(s)
    }
}
