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
package net.cloudopt.next.quartz;

import net.cloudopt.next.web.Plugin;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.List;

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
