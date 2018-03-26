package net.cloudopt.next.web.test.cookie

import io.vertx.core.Vertx
import io.vertx.ext.unit.TestSuite
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger


class CookieTest {
    val logger = LoggerFactory.getLogger(this::class.java.simpleName)

    @Test
    fun testSetCookie() {
        val int = AtomicInteger(0)

        val vertx = Vertx.vertx()
        TestSuite.create("cookie_test").test("cookie", 1000) { context ->
            val client = vertx.createHttpClient()
            val inc = int.incrementAndGet()
            logger.info("start $inc  ")
            client.getNow(9000, "localhost", "/cookie/$inc") { resp ->
                if (resp.statusCode() == 200) {
                    resp.bodyHandler {
                        logger.info("end $inc == ${resp.request().absoluteURI().split("/").last()} ")
                        context.assertEquals(resp.request().absoluteURI().split("/").last(), it.toString())
                        client.close()
                    }.exceptionHandler {
                        context.fail("post not success")
                    }

                } else {
                    context.fail("post not success")
                }

            }


        }.run(vertx).awaitSuccess()
        Thread.sleep(10000)

    }

}

