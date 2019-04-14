package com.simplyti.service.exception;


import io.netty.handler.codec.http.HttpResponseStatus;

@SuppressWarnings("serial")
public class NotFoundException extends ServiceException {
	
	public NotFoundException() {
		super(HttpResponseStatus.NOT_FOUND);
	}

}
