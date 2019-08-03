package com.simplyti.service.clients.k8s.jobs;

import com.simplyti.service.clients.k8s.common.NamespacedK8sApi;
import com.simplyti.service.clients.k8s.jobs.builder.JobBuilder;
import com.simplyti.service.clients.k8s.jobs.domain.Job;

public interface NamespacedJobs  extends NamespacedK8sApi<Job> {

	JobBuilder builder();
	
}
