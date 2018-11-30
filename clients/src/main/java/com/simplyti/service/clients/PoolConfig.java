package com.simplyti.service.clients;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@AllArgsConstructor
public class PoolConfig {

	private final long maxIdle;

}
