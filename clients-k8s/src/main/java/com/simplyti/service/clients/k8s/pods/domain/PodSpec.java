package com.simplyti.service.clients.k8s.pods.domain;

import java.util.List;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class PodSpec {
	
	private final List<Container> containers;
	private final List<ImagePullSecret> imagePullSecrets;
	private final RestartPolicy restartPolicy;
	private final Integer terminationGracePeriodSeconds;

	@CompiledJson
	public PodSpec(
			List<Container> containers,
			RestartPolicy restartPolicy,
			List<ImagePullSecret> imagePullSecrets,
			Integer terminationGracePeriodSeconds) {
		this.containers=containers;
		this.restartPolicy=restartPolicy;
		this.imagePullSecrets=imagePullSecrets;
		this.terminationGracePeriodSeconds=terminationGracePeriodSeconds;
	}

}
