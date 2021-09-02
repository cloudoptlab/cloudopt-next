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
package net.cloudopt.next.quartz

import net.cloudopt.next.core.Plugin
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory

class QuartzPlugin : Plugin {

    companion object {
        val jobs: MutableList<JobBean> = mutableListOf()
        var scheduler: Scheduler = StdSchedulerFactory.getDefaultScheduler()
    }

    override fun start(): Boolean {
        for (job in jobs) {
            val j2: Class<Job> = Class.forName(job.jobClass) as Class<Job>
            val jobDetail = JobBuilder.newJob(j2).withIdentity(
                job.jobDesc,
                job.jobGroup
            ).build()
            jobDetail.jobDataMap["scheduleJob"] = job

            // Expression scheduling builder
            val scheduleBuilder = CronScheduleBuilder.cronSchedule(job.cronExpression).inTimeZone(
                job.timeZone
            )

            // Build a new trigger by the new cronExpression expression
            val trigger = TriggerBuilder.newTrigger().withIdentity(job.jobDesc, job.jobGroup)
                .withSchedule(scheduleBuilder).build()
            scheduler.scheduleJob(jobDetail, trigger)
        }
        scheduler.start()
        return true
    }

    override fun stop(): Boolean {
        scheduler.shutdown()
        return true
    }


    fun addJob(job: JobBean) {
        jobs.add(job)
    }


}