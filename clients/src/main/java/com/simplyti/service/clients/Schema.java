package com.simplyti.service.clients;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class Schema {
	
	private final String name;
	private final boolean ssl;
	private final int defaultPort;
	
}
