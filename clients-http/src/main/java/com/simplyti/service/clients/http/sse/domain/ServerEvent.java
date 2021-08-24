package com.simplyti.service.clients.http.sse.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@AllArgsConstructor
public class ServerEvent {

	private final String event;
	private final String id;
	private final String data;
	
}
