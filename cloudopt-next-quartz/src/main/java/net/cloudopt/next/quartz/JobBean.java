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

public class JobBean {

    /**
     * Job's id
     */
    private String jobId;

    /**
     * Job's description
     */
    private String jobDesc;

    /**
     * Job's runtime expression
     */
    private String cronExpression;

    /**
     * Job's group
     */
    private String jobGroup;

    /**
     * Job's class
     */
    private String jobClass;

    public JobBean(String jobId, String jobDesc, String cronExpression, String jobGroup, String jobClass) {
        this.jobId = jobId;
        this.jobDesc = jobDesc;
        this.cronExpression = cronExpression;
        this.jobGroup = jobGroup;
        this.jobClass = jobClass;
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
}