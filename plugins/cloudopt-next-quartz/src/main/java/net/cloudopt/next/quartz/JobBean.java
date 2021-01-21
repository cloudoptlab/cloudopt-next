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

public class JobBean {

    /**
     * Job's id.
     */
    private String jobId;

    /**
     * Job's description.
     */
    private String jobDesc;

    /**
     * Job's runtime expression.
     */
    private String cronExpression;

    /**
     * Job's group.
     */
    private String jobGroup;

    /**
     * Job's class.
     */
    private String jobClass;

    /**
     * The <code>TimeZone</code> in which to base the schedule.
     */
    private String timeZone;

    public JobBean(String jobId, String jobDesc, String cronExpression, String jobGroup, String jobClass, String timeZone) {
        this.jobId = jobId;
        this.jobDesc = jobDesc;
        this.cronExpression = cronExpression;
        this.jobGroup = jobGroup;
        this.jobClass = jobClass;
        this.timeZone = timeZone;
    }

    public JobBean() {
        super();
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}