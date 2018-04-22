package com.simplyti.service.clients;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class Address {
	
	private final String host;
	private final int port;
	
}
