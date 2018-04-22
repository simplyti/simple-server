package com.simplyti.service.client;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class ClientAddress {
	
	private final String host;
	private final int port;

}
