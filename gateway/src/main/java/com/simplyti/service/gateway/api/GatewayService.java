package com.simplyti.service.gateway.api;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class GatewayService {
	
	private Collection<GatewayEndpoint> endpoints;

}
