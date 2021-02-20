package net.cloudopt.next.utils

import org.junit.Test

class TestCase {

    @Test
    fun testGetRootClassPath() {
        println(Resourcer.getRootClassPath())
    }

    @Test
    fun testReadJsonFile() {
        println(Resourcer.read("application.json"))
    }

    @Test
    fun testReadJsonFileByPrefix() {
        println(Resourcer.read("application.json", "vertx"))
    }

    @Test
    fun testReadJsonFileByPrefixToObject() {
        println(Resourcer.read("application.json", "waf", WafConfigBean::class))
    }

}