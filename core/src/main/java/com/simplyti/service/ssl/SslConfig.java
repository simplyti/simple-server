package com.simplyti.service.ssl;

import io.netty.handler.ssl.SslProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent=true)
public class SslConfig {
	
	private final SslProvider sslProvider;

}
