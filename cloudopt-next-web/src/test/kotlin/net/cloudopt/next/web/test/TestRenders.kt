package net.cloudopt.next.web.test

import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import net.cloudopt.next.client.HttpClient
import kotlin.test.Test
import kotlin.test.assertTrue

class TestRenders : TestStart() {

    private var client = HttpClient("http://127.0.0.1").setPort(8080)

    @Test
    fun testText() = runBlocking {
        val httpCode = client.get("/render/text").send().await().statusCode()
        assertTrue {
            httpCode == 200
        }
    }

    @Test
    fun testJson() = runBlocking {
        val httpCode = client.get("/render/json").send().await().statusCode()
        assertTrue {
            httpCode == 200
        }
    }

    @Test
    fun testHtml() = runBlocking {
        val httpCode = client.get("/render/html").send().await().statusCode()
        assertTrue {
            httpCode == 200
        }
    }

    @Test
    fun testFree() = runBlocking {
        val httpCode = client.get("/render/free").send().await().statusCode()
        assertTrue {
            httpCode == 200
        }
    }

    @Test
    fun testHbs() = runBlocking {
        val httpCode = client.get("/render/hbs").send().await().statusCode()
        assertTrue {
            httpCode == 200
        }
    }

}