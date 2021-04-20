package net.cloudopt.next.web.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.cloudopt.next.web.Worker
import net.cloudopt.next.web.Worker.async
import net.cloudopt.next.web.Worker.await
import net.cloudopt.next.web.Worker.global
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class TestAwait {
    @ExperimentalCoroutinesApi
    @BeforeTest
    fun init() {
        Dispatchers.setMain(Worker.dispatcher())
    }

    @ExperimentalCoroutinesApi
    @AfterTest
    fun clear() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAwaitOne() = runBlocking {
        val text = await {
            return@await "success"
        }
        assert(text == "success")
    }

    @Test
    fun testAwaitTwo() = runBlocking {
        val text = await<String> { promise ->
            promise.complete("success")
        }
        assert(text == "success")
    }

    @Test
    fun testAwaitInGlobalOne() {
        global {
            val text = await {
                return@await "success"
            }
            assert(text == "success")
        }
    }

    @Test
    fun testAwaitInGlobalTwo() {
        global {
            val text = await<String> { promise ->
                promise.complete("success")
            }
            assert(text == "success")
        }
    }

    @Test
    fun testAwaitInRunBlocking() {
        runBlocking {
            val text = await<String> { promise ->
                promise.complete("success")
            }
            assert(text == "success")
        }
    }

    @Test
    fun testAsync() {
        val text = async {
            return@async "success"
        }
        assert(text == "success")
    }

    @Test
    fun testAsyncFunction(){
        val text = asyncFunction()
        assert(text == "success")
    }

    fun asyncFunction():String = async {
        return@async await {
            return@await "success"
        }
    }


}