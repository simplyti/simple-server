package com.simplyti.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@AllArgsConstructor
public class Listener {
	
	private final int port;
	private final boolean ssl;

}
