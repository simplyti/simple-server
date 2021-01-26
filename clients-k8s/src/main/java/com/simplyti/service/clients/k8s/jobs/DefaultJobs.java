package com.simplyti.service.clients.k8s.jobs;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.jobs.domain.Job;

import io.netty.channel.EventLoopGroup;

public class DefaultJobs extends DefaultK8sApi<Job> implements Jobs {

	private static final String RESOURCE = "jobs";
	private static final TypeLiteral<KubeList<Job>> LIST_TYPE = new TypeLiteral<KubeList<Job>>() {};
	private static final TypeLiteral<Event<Job>> EVENT_TYPE = new TypeLiteral<Event<Job>>() {};

	public DefaultJobs(EventLoopGroup eventLoopGroup, HttpClient http, long timeoutMillis, Json json) {
		super(eventLoopGroup,http,timeoutMillis,json,K8sAPI.V1,RESOURCE,LIST_TYPE,EVENT_TYPE);
	}

	@Override
	public NamespacedJobs namespace(String namespace) {
		return new DefaultNamespacedJobs(eventLoopGroup(),http(),timeoutMillis(),json(),K8sAPI.BATCH1,RESOURCE,LIST_TYPE,EVENT_TYPE,namespace);
	}

}
