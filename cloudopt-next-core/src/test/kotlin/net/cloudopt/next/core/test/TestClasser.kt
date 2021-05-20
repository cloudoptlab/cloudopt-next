package net.cloudopt.next.core.test

import net.cloudopt.next.core.Classer
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestClasser {

    private val packageName = "net.cloudopt.next.core.test"

    @Test
    fun testScanPackageByAnnotation() {
        assertFalse {
            Classer.scanPackageByAnnotation(packageName, true, TestAnnotation::class).isEmpty()
        }
    }


    @Test
    fun testScanPackageBySuper() {
        assertTrue {
            Classer.scanPackageBySuper(packageName, true, WafConfigBean::class).isEmpty()
        }
        assertFalse {
            Classer.scanPackageBySuper(packageName, true, TestSuperClass::class).isEmpty()
        }
    }

    @Test
    fun testScanPackage() {
        assertTrue {
            Classer.getClassPaths("net.cloudopt.next.core.test").isNotEmpty()
        }
    }

    @Test
    fun testLoadClass() {
        assertNotNull(
            Classer.loadClass("net.cloudopt.next.core.test.TestSuperClass")
        )
        assertThrows<NullPointerException> {
            Classer.loadClass("net.cloudopt.next.core.test.ErrorName")
        }
    }


}