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
package net.cloudopt.next.quartz.test

import net.cloudopt.next.quartz.JobBean
import net.cloudopt.next.quartz.QuartzPlugin
import net.cloudopt.next.web.CloudoptServer
import org.junit.Test


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