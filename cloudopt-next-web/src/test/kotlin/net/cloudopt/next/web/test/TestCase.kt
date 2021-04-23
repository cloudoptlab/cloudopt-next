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
package net.cloudopt.next.web.test

import io.vertx.core.Handler
import net.cloudopt.next.web.NextServer
import net.cloudopt.next.core.Worker
import net.cloudopt.next.core.ConfigManager
import net.cloudopt.next.json.Jsoner.toJsonString
import net.cloudopt.next.web.event.EventPlugin
import net.cloudopt.next.web.health.HealthChecksManager
import net.cloudopt.next.web.health.HealthChecksPlugin
import net.cloudopt.next.web.health.indicators.DiskSpaceHealthIndicator
import net.cloudopt.next.web.health.indicators.JvmHealthIndicator
import net.cloudopt.next.web.health.indicators.SystemIndicator
import net.cloudopt.next.web.test.plugin.TestPlugin

/*
 * @author: Cloudopt
 * @Time: 2018/1/17
 * @Description: Test Case
 */
fun main(args: Array<String>) {
    println(NextServer.webConfig.toJsonString())
    NextServer.addPlugin(TestPlugin())
    NextServer.addPlugin(EventPlugin())
    HealthChecksManager.register("disk",DiskSpaceHealthIndicator())
    HealthChecksManager.register("jvm",JvmHealthIndicator())
    HealthChecksManager.register("system",SystemIndicator())
//    HealthChecksManager.registerHook("logger",LoggerHook())
    NextServer.addPlugin(HealthChecksPlugin())
    NextServer.run(Student::class)
    Worker.setTimer(1000, false, Handler { id ->
        println("And one second later that is printed: $id")
    })
    Worker.cancelTimer(1)
}
