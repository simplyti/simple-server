package com.simplyti.service.clients.k8s.pods.updater;

import com.simplyti.service.clients.k8s.pods.domain.Pod;

import io.netty.util.concurrent.Future;

public interface PodUpdater {
	
	PodUpdater deleteLabel(String name);
	PodUpdater addLabel(String name, String value);
	Future<Pod> update();

}
