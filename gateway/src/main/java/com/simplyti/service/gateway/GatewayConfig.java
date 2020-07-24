package com.simplyti.service.gateway;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent=true)
public class GatewayConfig {
	
	private final long maxChannelIdleTimeout;

	private final boolean keepOriginalHost;
	
	private int releaseChannelGraceTime;

}
