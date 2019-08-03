package com.simplyti.service.clients.k8s.jobs.builder;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.Metadata;
import com.simplyti.service.clients.k8s.common.builder.AbstractK8sResourceBuilder;
import com.simplyti.service.clients.k8s.jobs.domain.Job;
import com.simplyti.service.clients.k8s.jobs.domain.JobSpec;
import com.simplyti.service.clients.k8s.jobs.domain.PodTemplateSpec;
import com.simplyti.service.clients.k8s.pods.builder.ContainerBuilder;
import com.simplyti.service.clients.k8s.pods.builder.ContainerHolder;
import com.simplyti.service.clients.k8s.pods.builder.DefaultContainerBuilder;
import com.simplyti.service.clients.k8s.pods.domain.Container;
import com.simplyti.service.clients.k8s.pods.domain.PodSpec;
import com.simplyti.service.clients.k8s.pods.domain.RestartPolicy;

public class DefaultJobBuilder extends AbstractK8sResourceBuilder<JobBuilder,Job> implements JobBuilder, ContainerHolder {

	public static final String KIND = "Job";
	
	private List<Container> containers;
	private RestartPolicy restartPolicy = RestartPolicy.OnFailure;
	
	public DefaultJobBuilder(HttpClient client,Json json, K8sAPI api,String namespace, String resource) {
		super(client,json,api,namespace,resource,Job.class);
	}

	@Override
	public ContainerBuilder<JobBuilder> withContainer() {
		return new DefaultContainerBuilder<>(this,this);
	}

	@Override
	protected Job resource(K8sAPI api, Metadata metadata) {
		return new Job(KIND, api.version(), metadata, new JobSpec(new PodTemplateSpec(new PodSpec(containers,restartPolicy, null))),null);
	}
	
	@Override
	public void addContainer(Container container) {
		if(containers==null) {
			this.containers=new ArrayList<>();
		}
		this.containers.add(container);
	}

}
