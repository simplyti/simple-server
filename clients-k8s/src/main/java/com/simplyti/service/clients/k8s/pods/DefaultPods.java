package com.simplyti.service.clients.k8s.pods;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.pods.domain.Pod;

import io.netty.channel.EventLoopGroup;

public class DefaultPods extends DefaultK8sApi<Pod> implements Pods {
	
	private static final String RESOURCE = "pods";
	private static final TypeLiteral<KubeList<Pod>> LIST_TYPE = new TypeLiteral<KubeList<Pod>>() {};
	private static final TypeLiteral<Event<Pod>> EVENT_TYPE = new TypeLiteral<Event<Pod>>() {};

	public DefaultPods(EventLoopGroup eventLoopGroup, HttpClient http, Json json) {
		super(eventLoopGroup,http,json,K8sAPI.V1,RESOURCE,LIST_TYPE,EVENT_TYPE);
	}

	@Override
	public NamespacedPods namespace(String namespace) {
		return new DefaultNamespacedPods(eventLoopGroup(),http(),json(),K8sAPI.V1,RESOURCE,LIST_TYPE,EVENT_TYPE,namespace);
	}

}
