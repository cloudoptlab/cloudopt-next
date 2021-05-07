package net.cloudopt.next.eventbus.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.cloudopt.next.core.Worker
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class TestStart {

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun startServer() = runBlocking {
        Dispatchers.setMain(Worker.dispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun stopServer() {
        Dispatchers.resetMain()
    }

}