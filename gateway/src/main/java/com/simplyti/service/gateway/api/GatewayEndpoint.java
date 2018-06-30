package com.simplyti.service.gateway.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class GatewayEndpoint {
	
	private String url;

}
