package com.simplyti.service.clients.http.exception;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent=true)
@Getter
@SuppressWarnings("serial")
public class HttpException extends RuntimeException {

	private final int code;

	public HttpException(int code) {
		super("Unexpected code: "+code);
		this.code=code;
	}

}
