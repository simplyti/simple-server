package com.simplyti.service.clients.k8s.jobs;

import com.simplyti.service.clients.k8s.common.K8sApi;
import com.simplyti.service.clients.k8s.common.Namespaced;
import com.simplyti.service.clients.k8s.jobs.domain.Job;

public interface Jobs extends Namespaced<Job,NamespacedJobs>, K8sApi<Job>{

}
