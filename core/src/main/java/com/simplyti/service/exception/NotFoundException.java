package com.simplyti.service.exception;


import io.netty.handler.codec.http.HttpResponseStatus;

@Deprecated()
@SuppressWarnings("serial")
public class NotFoundException extends ServiceException {
	
	public NotFoundException() {
		super(HttpResponseStatus.NOT_FOUND);
	}

}
