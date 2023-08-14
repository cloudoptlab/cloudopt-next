/*
 * Copyright 2017-2021 Cloudopt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cloudopt.next.core

import io.vertx.core.*
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.awaitBlocking
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.*
import net.cloudopt.next.logging.Logger

object Worker {

    open val vertxOptions: VertxOptions = ConfigManager.init("vertx").toObject(VertxOptions::class)

    @JvmStatic
    open var vertx: Vertx = Vertx.vertx(vertxOptions)

    private val logger = Logger.getLogger(this::class)


    /**
     * By default, if executeBlocking is called several times from the same context (e.g. the same verticle instance)
     * then the different executeBlocking are executed serially (i.e. one after another).If you don’t care about
     * ordering you can call the function.
     *
     * @param handler handler representing the blocking code to run
     * @param resultHandler handler that will be called when the blocking code is complete
     */
    @JvmOverloads
    fun <T> worker(
        handler: Handler<Promise<T>>, resultHandler: Handler<AsyncResult<T>> = Handler<AsyncResult<T>> {}
    ) {
        vertx.executeBlocking(handler, resultHandler)
    }

    /**
     * By default, if executeBlocking is called several times from the same context (e.g. the same verticle instance)
     * then the different executeBlocking are executed serially (i.e. one after another).If you don’t care about
     * ordering you can call the function.
     *
     * If using await, the call must be completed manually before
     * it will end.
     *
     * @param block handler representing the blocking code to run
     */
    suspend fun <T> await(block: Handler<Promise<T>>): T {
        return vertx.executeBlocking(block).await()
    }

    /**
     * By default, if executeBlocking is called several times from the same context (e.g. the same verticle instance)
     * then the different executeBlocking are executed serially (i.e. one after another).If you don’t care about
     * ordering you can call the function.
     *
     * If using await, the call must be completed manually before
     * it will end.
     *
     * @param block handler representing the blocking code to run
     */
    suspend fun <T> await(block: () -> T): T {
        return await { promise ->
            promise.complete(block.invoke())
        }
    }

    /**
     * Puts the handler on the event queue for the current context so it will be run asynchronously ASAP after all
     * preceeding events have been handled.
     *
     * @param block - a handler representing the action to execute
     */
    fun then(block: Handler<Void>) {
        vertx.runOnContext(block)
    }

    /**
     * Awaits for completion of given deferred values without blocking a thread and resumes normally with the list of values
     * when all deferred computations are complete or resumes with the first thrown exception if any of computations
     * complete exceptionally including cancellation.
     */
    suspend fun <T> gather(vararg blocks: suspend CoroutineScope.() -> T): List<T> {
        val list = mutableListOf<Deferred<T>>()
        coroutineScope {
            blocks.forEach { block ->
                list.add(async {
                    return@async block.invoke(this)
                })
            }
        }
        return list.awaitAll()
    }

    /**
     * Automatic deployment in vertx.
     *
     * @param name Package name
     * @param deploymentOptions   Options for configuring a verticle deployment
     */
    @JvmOverloads
    fun deploy(
        name: String,
        deploymentOptions: DeploymentOptions = ConfigManager.init("vertxDeployment")
            .toObject(DeploymentOptions::class), workerPoolName: String = "net.cloudopt.next"
    ) {
        deploymentOptions.workerPoolName = workerPoolName
        vertx.deployVerticle(name, deploymentOptions).onComplete {res ->
            if (res.succeeded()){
                logger.info("[WORKER] The $name verticle is successfully deployed.")
                return@onComplete
            }else if(res.cause() != null){
                throw res.cause()
            }
            logger.error("[WORKER] Failed to deploy $name verticle.")
        }
    }


    /**
     * Automatic undeploy verticle in vertx.
     * @param verticle Package name
     */
    fun undeploy(verticle: String) {
        vertx.undeploy(verticle)
    }

    /**
     * Set a one-shot timer to fire after {@code delay} milliseconds, at which point {@code handler} will be called with
     * the id of the timer. If periodic is true, set a periodic timer to fire every {@code delay} milliseconds, at which
     * point {@code handler} will be called with the id of the timer.
     * @param delay  the delay in milliseconds, after which the timer will fire
     * @param periodic If periodic is true, set a periodic timer
     * @param handler  the handler that will be called with the timer ID when the timer fires
     * @return the unique ID of the timer
     */
    fun setTimer(delay: Long, periodic: Boolean = false, handler: Handler<Long>): Long {
        return if (periodic) {
            vertx.setPeriodic(delay, handler)
        } else {
            vertx.setTimer(delay, handler)
        }
    }

    /**
     * Cancels the timer with the specified {@code id}.
     *
     * @param id  The id of the timer to cancel
     * @return true if the timer was successfully cancelled, or false if the timer does not exist.
     */
    fun cancelTimer(id: Long): Boolean {
        return vertx.cancelTimer(id)
    }

    /** Returns a coroutine dispatcher for the current Vert.x context. It uses the Vert.x context event loop. */
    fun dispatcher(): CoroutineDispatcher {
        return vertx.dispatcher()
    }

    /**
     * Launches a new coroutine without blocking the current thread and returns a reference to the coroutine as a Job.
     * The coroutine is cancelled when the resulting job is cancelled.
     * @param block [@kotlin.ExtensionFunctionType] SuspendFunction1<CoroutineScope, Unit>
     */
    fun <T> global(block: suspend CoroutineScope.() -> T) {
        GlobalScope.launch(dispatcher()) {
            block.invoke(this)
        }
    }

    /**
     * Stop the Vertx instance and release any resources held by it. The instance cannot be used after it has been
     * closed.
     */
    fun close() {
        vertx.close()
    }

}
