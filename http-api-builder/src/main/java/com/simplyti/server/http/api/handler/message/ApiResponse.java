package com.simplyti.server.http.api.handler.message;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class ApiResponse {
	
	private final boolean keepAlive;
	private final boolean notFoundOnNull;
	
	public abstract Object message();

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public boolean notFoundOnNull() {
		return notFoundOnNull;
	}
	
}
