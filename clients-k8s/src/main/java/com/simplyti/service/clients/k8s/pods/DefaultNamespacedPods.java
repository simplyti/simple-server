package com.simplyti.service.clients.k8s.pods;

import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultNamespacedK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.pods.domain.Pod;

import io.netty.channel.EventLoopGroup;

public class DefaultNamespacedPods extends DefaultNamespacedK8sApi<Pod> implements NamespacedPods {

	public DefaultNamespacedPods(EventLoopGroup eventLoopGroup,HttpClient http, K8sAPI api, String resource, TypeLiteral<KubeList<Pod>> listType, 
			TypeLiteral<Event<Pod>> eventType, String namespace) {
		super(eventLoopGroup,http,api,namespace,resource,Pod.class,listType,eventType);
	}

}
