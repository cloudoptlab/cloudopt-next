/*
 * Copyright 2017 Cloudopt.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package net.cloudopt.next.web

import io.vertx.core.*
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
     * @param handler Do something..
     * @param queueResult After the completion of the callback
     */
    @JvmStatic
    fun then(handler: Handler<Future<Any>>,
             queueResult: Handler<AsyncResult<Any>>) {
        CloudoptServer.vertx.executeBlocking(handler, queueResult)
    }

    /**
     * By default, if executeBlocking is called several times from
     * the same context (e.g. the same verticle instance) then the
     * different executeBlocking are executed serially (i.e. one
     * after another).If you don’t care about ordering you can call
     * the function.
     * @param queueResult After the completion of the callback
     */
    @JvmStatic
    fun worker(handler: Handler<Future<Any>>,
               queueResult: Handler<AsyncResult<Any>>) {
        CloudoptServer.vertx.executeBlocking(handler, false, queueResult)
    }

    /**
     * Automatic deployment in vertx.
     * @param verticle Package name
     * @param worker Run with a separate thread pool
     */
    @JvmStatic
    @JvmOverloads
    fun deploy(verticle: String, worker: Boolean = false) {
        var options = CloudoptServer.deploymentOptions
        if(worker){
            options = DeploymentOptions(options)
            options.setWorker(worker)
        }
        CloudoptServer.vertx.deployVerticle(verticle, options)
    }


    /**
     * Automatic undeployment in vertx.
     * @param verticle Package name
     */
    @JvmStatic
    fun undeploy(verticle: String) {
        CloudoptServer.vertx.undeploy(verticle)
    }
}
