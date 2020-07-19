/*
 * Copyright 2017-2020 original authors
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

import io.vertx.core.AsyncResult
import io.vertx.core.DeploymentOptions
import io.vertx.core.Handler
import io.vertx.core.Promise
import net.cloudopt.next.web.config.ConfigManager

/*
 * @author: Cloudopt
 * @Time: 2018/1/16
 * @Description: Vertx tool class
 */

object Worker {

    /**
     * It’s done by calling executeBlocking specifying both
     * the blocking code to execute and a result handler to
     * be called back asynchronous when the blocking code has
     * been executed.
     *
     * @param handler     Do something..
     * @param queueResult After the completion of the callback
     */
    @JvmOverloads
    fun <T> then(
            handler: Handler<Promise<Any>>,
            queueResult: Handler<AsyncResult<Any>>
    ) {
        CloudoptServer.vertx.executeBlocking(handler, queueResult)
    }

    /**
     * By default, if executeBlocking is called several times from
     * the same context (e.g. the same verticle instance) then the
     * different executeBlocking are executed serially (i.e. one
     * after another).If you don’t care about ordering you can call
     * the function.
     *
     * @param queueResult After the completion of the callback
     */
    @JvmOverloads
    fun <T> worker(
            handler: Handler<Promise<Any>>,
            queueResult: Handler<AsyncResult<Any>>
    ) {
        CloudoptServer.vertx.executeBlocking(handler, false, queueResult)
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
        CloudoptServer.vertx.deployVerticle(verticle, options)
    }


    /**
     * Automatic undeployment in vertx.
     * @param verticle Package name
     */
    fun unploy(verticle: String) {
        CloudoptServer.vertx.undeploy(verticle)
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
        if(periodic){
            CloudoptServer.vertx.setPeriodic(delay, handler)
        }else{
            CloudoptServer.vertx.setTimer(delay, handler)
        }
    }

    /**
     * Cancels the timer with the specified {@code id}.
     *
     * @param id  The id of the timer to cancel
     * @return true if the timer was successfully cancelled, or false if the timer does not exist.
     */
    fun cancelTimer(id: Long) {
        CloudoptServer.vertx.cancelTimer(id)
    }

}
