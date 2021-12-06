package net.cloudopt.next.web.test

import io.vertx.kotlin.core.json.get
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.runBlocking
import net.cloudopt.next.client.HttpClient
import kotlin.test.Test
import kotlin.test.assertTrue

class TestRestful : TestStart() {

    private var client = HttpClient("http://127.0.0.1").setPort(8080)

    @Test
    fun testGet() = runBlocking {
        val httpCode = client.get("/restful").send().await().statusCode()
        assertTrue {
            httpCode == 200
        }
    }

    @Test
    fun testPost() = runBlocking {
        val httpCode = client.post("/restful").send().await().statusCode()
        assertTrue {
            httpCode == 200
        }
    }

    @Test
    fun testPut() = runBlocking {
        val httpCode = client.put("/restful").send().await().statusCode()
        assertTrue {
            httpCode == 200
        }
    }

    @Test
    fun testDelete() = runBlocking {
        val httpCode = client.delete("/restful").send().await().statusCode()
        assertTrue {
            httpCode == 200
        }
    }

    @Test
    fun testPatch() = runBlocking {
        val httpCode = client.patch("/restful").send().await().statusCode()
        assertTrue {
            httpCode == 200
        }
    }

    @Test
    fun testDefaultError() = runBlocking {
        val httpCode = client.get("/restful/defaultError").send().await().statusCode()
        assertTrue {
            httpCode == 402
        }
    }

    @Test
    fun testCustomError() = runBlocking {
        val result = client.get("/restful/customError").putHeader("Content-Type", "application/json").send().await()
            .bodyAsJsonObject()
        assertTrue {
            result.get<String>("errorMessage") == "Test Error"
        }
    }

}