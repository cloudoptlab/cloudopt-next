package net.cloudopt.next.quartz.test

import org.quartz.Job
import org.quartz.JobExecutionContext

class Task1: Job {
    override fun execute(context: JobExecutionContext?) {
        println("Hello!")
    }

}