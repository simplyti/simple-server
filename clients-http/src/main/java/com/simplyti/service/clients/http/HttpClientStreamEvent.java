package com.simplyti.service.clients.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class HttpClientStreamEvent {
	
	public static final HttpClientStreamEvent STOP = new HttpClientStreamEvent(Type.STOP,false);

	public enum Type {
		START, STOP
	}
	
	private final Type type ;
	private final boolean checkStatus;
	
}
