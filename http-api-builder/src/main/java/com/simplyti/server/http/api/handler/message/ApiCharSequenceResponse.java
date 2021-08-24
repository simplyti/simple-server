package com.simplyti.server.http.api.handler.message;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class ApiCharSequenceResponse extends ApiResponse {

	private final CharSequence message;

	public ApiCharSequenceResponse(String message, boolean keepAlive, boolean notFoundOnNull) {
		super(keepAlive, notFoundOnNull);
		this.message=message;
	}
	
	
}
