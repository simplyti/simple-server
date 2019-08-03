package com.simplyti.service.clients.k8s.jobs;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultNamespacedK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.jobs.builder.DefaultJobBuilder;
import com.simplyti.service.clients.k8s.jobs.builder.JobBuilder;
import com.simplyti.service.clients.k8s.jobs.domain.Job;

import io.netty.channel.EventLoopGroup;

public class DefaultNamespacedJobs extends DefaultNamespacedK8sApi<Job> implements NamespacedJobs {

	public DefaultNamespacedJobs(EventLoopGroup eventLoopGroup,HttpClient http,Json json, K8sAPI api, String resource, TypeLiteral<KubeList<Job>> listType, 
			TypeLiteral<Event<Job>> eventType, String namespace) {
		super(eventLoopGroup,http,json,api,namespace,resource,Job.class,listType,eventType);
	}
	
	@Override
	public JobBuilder builder() {
		return new DefaultJobBuilder(http(),json(), api(), namespace(), resource());
	}

}
