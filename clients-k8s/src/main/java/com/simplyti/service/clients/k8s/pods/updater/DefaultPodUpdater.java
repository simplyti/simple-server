package com.simplyti.service.clients.k8s.pods.updater;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.updater.AbstractK8sResourceUpdater;
import com.simplyti.service.clients.k8s.common.updater.JsonPatch;
import com.simplyti.service.clients.k8s.pods.domain.Pod;

public class DefaultPodUpdater extends AbstractK8sResourceUpdater<Pod> implements PodUpdater {
	
	public DefaultPodUpdater(HttpClient client, Json json, K8sAPI api, String namespace, String resource, String name) {
		super(client,json, api, namespace, resource, name, Pod.class);
	}

	@Override
	public PodUpdater deleteLabel(String name) {
		addPatch(JsonPatch.remove("/metadata/labels/"+name));
		return this;
	}

	@Override
	public PodUpdater addLabel(String name, String value) {
		addPatch(JsonPatch.add("/metadata/labels/"+name,value));
		return this;
	}

}
