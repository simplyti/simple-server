package com.simplyti.service.clients;

import com.google.common.base.Joiner;

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
	
	@Override
	public String toString() {
		return Joiner.on(':').join(host,port);
	}
	
}
