package net.cloudopt.next.core.test

import net.cloudopt.next.core.Resourcer
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test

class TestResource {

    @Test
    fun testGetFile() {
        assertDoesNotThrow {
            Resourcer.getFile("application.json")
        }
    }

    @Test
    fun testgetUrl() {
        assertDoesNotThrow {
            Resourcer.getUrl("application.json")
        }
    }

    @Test
    fun testGetInputStream() {
        assertDoesNotThrow {
            val stream = Resourcer.getFileInputStream("application.json")
            Resourcer.inputStreamToString(stream)
        }
    }

    @Test
    fun testGetFileString() {
        assertDoesNotThrow {
            Resourcer.getFileString("application.json", true)
        }
    }

    @Test
    fun testRead() {
        assertDoesNotThrow {
            Resourcer.read("application.json", "waf", WafConfigBean::class)
        }

        assertDoesNotThrow {
            Resourcer.read("application.json", "waf")
        }
    }

}