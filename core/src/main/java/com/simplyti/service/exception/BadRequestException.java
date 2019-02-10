package com.simplyti.service.exception;


import io.netty.handler.codec.http.HttpResponseStatus;

@Deprecated
@SuppressWarnings("serial")
public class BadRequestException extends ServiceException {
	
	public BadRequestException() {
		super(HttpResponseStatus.BAD_REQUEST);
	}

}
