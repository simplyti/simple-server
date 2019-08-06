package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.EnvironmentVariable;

public interface ContainerEnvironmentHolder {

	void setEnvironment(EnvironmentVariable value);

}
