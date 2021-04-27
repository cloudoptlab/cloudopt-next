package net.cloudopt.next.eventbus.test

import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import net.cloudopt.next.client.HttpClient
import kotlin.test.Test
import kotlin.test.assertTrue

class TestCase :TestStart(){

    private var client = HttpClient("http://127.0.0.1").setPort(8080)

    @Test
    fun testSend() = runBlocking{
        val httpCode = client.post("/eventbus/send").send().await().statusCode()
        assertTrue {
            httpCode == 200
        }
    }

    @Test
    fun testPublish() = runBlocking{
        val httpCode = client.post("/eventbus/publish").send().await().statusCode()
        assertTrue {
            httpCode == 200
        }
    }

    @Test
    fun testAfterEvent() = runBlocking{
        val httpCode = client.post("/eventbus/after").send().await().statusCode()
        assertTrue {
            httpCode == 200
        }
    }

}