package com.simplyti.service.clients.k8s.services.updater;


import com.simplyti.service.clients.k8s.services.builder.ServicePortBuilder;
import com.simplyti.service.clients.k8s.services.domain.Service;

import io.netty.util.concurrent.Future;

public interface ServicesUpdater {

	ServicePortBuilder<? extends ServicesUpdater> setPort();

	Future<Service> update();

}
