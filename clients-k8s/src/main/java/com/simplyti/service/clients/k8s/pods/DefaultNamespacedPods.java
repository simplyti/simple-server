package com.simplyti.service.clients.k8s.pods;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultNamespacedK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.pods.builder.DefaultPodBuilder;
import com.simplyti.service.clients.k8s.pods.builder.PodBuilder;
import com.simplyti.service.clients.k8s.pods.domain.Pod;
import com.simplyti.service.clients.k8s.pods.updater.DefaultPodUpdater;
import com.simplyti.service.clients.k8s.pods.updater.PodUpdater;

import io.netty.channel.EventLoopGroup;

public class DefaultNamespacedPods extends DefaultNamespacedK8sApi<Pod> implements NamespacedPods {

	private final EventLoopGroup eventLoopGroup;
	private final HttpClient http;
	private final String resource;
	private final String namespace;
	private final K8sAPI api;
	
	public DefaultNamespacedPods(EventLoopGroup eventLoopGroup,HttpClient http,Json json, K8sAPI api, String resource, TypeLiteral<KubeList<Pod>> listType, 
			TypeLiteral<Event<Pod>> eventType, String namespace) {
		super(eventLoopGroup,http,json,api,namespace,resource,Pod.class,listType,eventType);
		this.eventLoopGroup=eventLoopGroup;
		this.http=http;
		this.resource=resource;
		this.namespace=namespace;
		this.api=api;
	}
	
	@Override
	public PodBuilder builder() {
		return new DefaultPodBuilder(http(),json(), api(), namespace(), resource());
	}

	@Override
	public PodUpdater update(String name) {
		return new DefaultPodUpdater(http(),json(), api(),namespace(),resource(),name);
	}

	@Override
	public LogStream log(String name) {
		return new DefaultLogStream(eventLoopGroup.next(), api,http,name,namespace,resource);
	}

}
