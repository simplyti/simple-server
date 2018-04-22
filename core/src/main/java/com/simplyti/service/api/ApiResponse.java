package com.simplyti.service.api;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiResponse {
	
	private final Object response;
	private final boolean keepAlive;

	public Object response() {
		return response;
	}
	
	public boolean isKeepAlive() {
		return keepAlive;
	}
	
}
