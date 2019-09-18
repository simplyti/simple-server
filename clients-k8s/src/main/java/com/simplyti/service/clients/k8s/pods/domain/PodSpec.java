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
	private final List<Volume> volumes;

	@CompiledJson
	public PodSpec(
			List<Container> containers,
			RestartPolicy restartPolicy,
			List<ImagePullSecret> imagePullSecrets,
			Integer terminationGracePeriodSeconds,
			List<Volume> volumes) {
		this.containers=containers;
		this.restartPolicy=restartPolicy;
		this.imagePullSecrets=imagePullSecrets;
		this.terminationGracePeriodSeconds=terminationGracePeriodSeconds;
		this.volumes=volumes;
	}

}
