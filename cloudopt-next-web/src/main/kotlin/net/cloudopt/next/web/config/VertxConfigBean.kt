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
package net.cloudopt.next.web.config

/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Vertx configuration file
 */
data class VertxConfigBean(
    var cpuCoreNumber: Int = Runtime.getRuntime().availableProcessors(),
    var maxWokerExecuteTime: Long = 60L * 1000 * 1000000,
    var fileCaching: Boolean = false,
    var workerPoolSize: Int = 10 * cpuCoreNumber,
    var eventLoopPoolSize: Int = 10 * cpuCoreNumber,
    var internalBlockingPoolSize: Int = 20,
    var clustered: Boolean = false,
    var clusterHost: String = "localhost",
    var clusterPort: Int = 0,
    var clusterPingInterval: Long = 20000,
    var clusterPingReplyInterval: Long = 20000,
    var blockedThreadCheckInterval: Long = 1000,
    var maxEventLoopExecuteTime: Long = 2L * 1000 * 1000000,
    var hAEnabled: Boolean = false,
    var hAGroup: String = "DEFAULT",
    var quorumSize: Int = 1,
    var warningExceptionTime: Long = 5L * 1000 * 1000000,
    var addressResolver: String = ""
)