package com.simplyti.service.clients.k8s.services.updater;


import com.simplyti.service.clients.k8s.services.builder.ServicePortBuilder;
import com.simplyti.service.clients.k8s.services.domain.Service;
import com.simplyti.service.clients.k8s.services.domain.ServiceType;

import io.netty.util.concurrent.Future;

public interface ServicesUpdater {

	ServicePortBuilder<? extends ServicesUpdater> setPort();
	ServicePortBuilder<? extends ServicesUpdater> addPort();

	Future<Service> update();

	ServicesUpdater setType(ServiceType clusterip);


}
