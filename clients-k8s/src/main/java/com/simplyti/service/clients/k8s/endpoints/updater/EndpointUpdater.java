package com.simplyti.service.clients.k8s.endpoints.updater;

import com.simplyti.service.clients.k8s.endpoints.builder.EndpointSubsetBuilder;
import com.simplyti.service.clients.k8s.endpoints.domain.Endpoint;
import com.simplyti.util.concurrent.Future;

public interface EndpointUpdater {

	EndpointSubsetBuilder<? extends EndpointUpdater> addSubset();
	EndpointSubsetBuilder<? extends EndpointUpdater> setSubset();
	EndpointUpdater clearSubsets();
	
	Future<Endpoint> update();

}
