package com.simplyti.service.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent=true)
@Builder
public class GatewayConfig {
	
	private final long maxChannelIdleTimeout;
	private final boolean keepOriginalHost;
	private final boolean clientMonitorEnabled;
	
}
