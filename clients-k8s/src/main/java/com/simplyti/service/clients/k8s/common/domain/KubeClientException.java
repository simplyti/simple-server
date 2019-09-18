package com.simplyti.service.clients.k8s.common.domain;

import lombok.Getter;
import lombok.experimental.Accessors;

@SuppressWarnings("serial")
@Accessors(fluent=true)
public class KubeClientException extends RuntimeException {

	@Getter
	private final Status status;

	public KubeClientException(Status status) {
		super(status.message());
		this.status=status;
	}

}
