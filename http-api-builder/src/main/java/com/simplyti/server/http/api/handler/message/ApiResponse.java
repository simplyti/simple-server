package com.simplyti.server.http.api.handler.message;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiResponse {
	
	private final Object response;
	private final boolean keepAlive;
	private final boolean notFoundOnNull;

	public Object response() {
		return response;
	}
	
	public boolean isKeepAlive() {
		return keepAlive;
	}

	public boolean notFoundOnNull() {
		return notFoundOnNull;
	}
	
}
