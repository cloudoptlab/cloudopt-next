package net.cloudopt.next.web.test

import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import net.cloudopt.next.client.HttpClient
import net.cloudopt.next.json.Jsoner.toJsonString
import kotlin.test.Test
import kotlin.test.assertTrue

class TestInterceptor:TestStart() {

    private var client = HttpClient("http://127.0.0.1").setPort(8080)

    @Test
    fun testInterceptor()= runBlocking {
        val cookies = client.get("/interceptor").send().await().cookies()
        println(cookies.toJsonString())
        assertTrue {
            cookies.contains("before=interceptor")
        }
    }

    @Test
    fun testValidator() = runBlocking{
        val httpCode = client.get("/interceptor/validator").send().await().statusCode()
        println(httpCode)
        assertTrue {
            httpCode == 400
        }
    }

    @Test
    fun testThrow() = runBlocking{
        val httpCode = client.get("/interceptor/throw").send().await().statusCode()
        println(httpCode)
        assertTrue {
            httpCode == 500
        }
    }

}