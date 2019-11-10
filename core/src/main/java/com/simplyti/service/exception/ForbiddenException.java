package com.simplyti.service.exception;


import io.netty.handler.codec.http.HttpResponseStatus;

@SuppressWarnings("serial")
public class ForbiddenException extends ServiceException {
	
	public ForbiddenException() {
		super(HttpResponseStatus.FORBIDDEN);
	}

}
