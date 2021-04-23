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
package net.cloudopt.next.quartz;

import net.cloudopt.next.core.Plugin;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class QuartzPlugin implements Plugin {

    private static Scheduler scheduler;
    private List<JobBean> jobs = new ArrayList();

    {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void addJob(JobBean job) {

        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobDesc(), job.getJobGroup());
        Trigger trigger = null;
        try {
            trigger = scheduler.getTrigger(triggerKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        // If it does not exist, create a new object
        if (trigger == null) {
            Class<Job> j2 = null;
            try {
                j2 = (Class<Job>) Class.forName(job.getJobClass());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            JobDetail jobDetail = JobBuilder.newJob(j2).withIdentity(job.getJobDesc(), job.getJobGroup()).build();
            jobDetail.getJobDataMap().put("scheduleJob", job);

            // Expression scheduling builder
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
            if (job.getTimeZone() != null && !job.getTimeZone().equals("")) {
                scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression()).inTimeZone(TimeZone.getTimeZone(job.getTimeZone()));
            }


            // Build a new trigger by the new cronExpression expression
            trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobDesc(), job.getJobGroup())
                    .withSchedule(scheduleBuilder).build();

            try {
                scheduler.scheduleJob(jobDetail, trigger);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        } else {
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
            TriggerBuilder tb = trigger.getTriggerBuilder();
            Trigger newTrigger = tb.withSchedule(scheduleBuilder).build();
            // Press new trigger to reset job execution
            try {
                scheduler.rescheduleJob(triggerKey, newTrigger);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public boolean start() {
        for (JobBean entry : jobs) {
            addJob(entry);
        }
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean stop() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return true;
    }
}
