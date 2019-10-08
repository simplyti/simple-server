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

import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;

public class DefaultNamespacedPods extends DefaultNamespacedK8sApi<Pod> implements NamespacedPods {

	public DefaultNamespacedPods(EventLoopGroup eventLoopGroup,HttpClient http,Json json, K8sAPI api, String resource, TypeLiteral<KubeList<Pod>> listType, 
			TypeLiteral<Event<Pod>> eventType, String namespace) {
		super(eventLoopGroup,http,json,api,namespace,resource,Pod.class,listType,eventType);
	}
	
	@Override
	public PodBuilder builder() {
		return new DefaultPodBuilder(http(),json(), api(), namespace(), resource());
	}

	@Override
	public Future<Pod> log(String name) {
		return http().request()
					.get(String.format("/api/v1/namespaces/%s/pods/%s/log", namespace(), name))
					.fullResponse(response -> pod(response.content()));
	}

	@Override
	public Future<Pod> log(String name, String container) {
		return http().request()
				.get(String.format("/api/v1/namespaces/%s/pods/%s/log", namespace(), name))
				.param("container", container)
				.fullResponse(response -> pod(response.content()));
	}

	private Pod pod(ByteBuf response) {
		return json().deserialize(response, Pod.class);
	}
}
