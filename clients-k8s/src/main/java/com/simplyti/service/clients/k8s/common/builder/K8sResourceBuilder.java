package com.simplyti.service.clients.k8s.common.builder;

import com.simplyti.service.clients.k8s.common.K8sResource;

import io.netty.util.concurrent.Future;

public interface K8sResourceBuilder<B extends K8sResourceBuilder<B,T>, T extends K8sResource> {
	
	B withName(String name);
	
	B withAnnotation(String ann, String value);
	
	B withLabel(String name, String value);
	
	Future<T> build();

}
