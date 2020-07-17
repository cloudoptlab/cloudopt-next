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
package net.cloudopt.next.quartz.test

import net.cloudopt.next.quartz.JobBean
import net.cloudopt.next.quartz.QuartzPlugin
import net.cloudopt.next.web.CloudoptServer


/*
 * @author: Cloudopt
 * @Time: 2018/2/7
 * @Description: Test Case
 */
fun main() {
    var plugin = QuartzPlugin()
    val job = JobBean()
    job.jobClass = "net.cloudopt.next.quartz.test.Task1"
    job.cronExpression = "* * * * * ? *"
    job.jobGroup = "TaskJob"
    job.jobDesc = "TaskJob"
    plugin.addJob(job)
    CloudoptServer.addPlugin(plugin)
    CloudoptServer.run()
}