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
package net.cloudopt.next.web

import io.vertx.core.*
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import net.cloudopt.next.web.config.ConfigManager

/*
 * @author: Cloudopt
 * @Time: 2018/1/16
 * @Description: Vertx tool class
 */

object Worker {

    @JvmStatic
    open var vertx: Vertx = Vertx.vertx(ConfigManager.config.vertx)

    /**
     * By default, if executeBlocking is called several times from
     * the same context (e.g. the same verticle instance) then the
     * different executeBlocking are executed serially (i.e. one
     * after another).If you don’t care about ordering you can call
     * the function.
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
     * By default, if executeBlocking is called several times from
     * the same context (e.g. the same verticle instance) then the
     * different executeBlocking are executed serially (i.e. one
     * after another).If you don’t care about ordering you can call
     * the function.
     *
     * If using await, the call must be completed manually before
     * it will end.
     *
     * @param handler handler representing the blocking code to run
     */
    suspend fun <T> awaitWorker(handler: Handler<Promise<T>>): T {
        return vertx.executeBlocking(handler).await()
    }

    /**
     * Automatic deployment in vertx.
     *
     * @param verticle Package name
     * @param worker   Run with a separate thread pool
     */
    @JvmOverloads
    fun deploy(verticle: String, worker: Boolean = false) {
        var options = ConfigManager.config.vertxDeployment
        if (worker) {
            options = DeploymentOptions(options)
            options.isWorker = worker
        }
        vertx.deployVerticle(verticle, options)
    }


    /**
     * Automatic undeployment in vertx.
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
    fun setTimer(delay: Long, periodic: Boolean, handler: Handler<Long>) {
        if (periodic) {
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
    fun cancelTimer(id: Long) {
        vertx.cancelTimer(id)
    }

    /**
     * Returns a coroutine dispatcher for the current Vert.x context.
     * It uses the Vert.x context event loop.
     */
    fun dispatcher(): CoroutineDispatcher {
        return vertx.dispatcher()
    }

    /**
     * Stop the the Vertx instance and release any resources held by it.
     * The instance cannot be used after it has been closed.
     */
    fun close() {
        vertx.close()
    }

}
