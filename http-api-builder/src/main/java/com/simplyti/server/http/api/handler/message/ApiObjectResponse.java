package com.simplyti.server.http.api.handler.message;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class ApiObjectResponse extends ApiResponse {

	private final Object message;

	public ApiObjectResponse(Object message, boolean keepAlive, boolean notFoundOnNull) {
		super(keepAlive, notFoundOnNull);
		this.message=message;
	}
	
	
}
