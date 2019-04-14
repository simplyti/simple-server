package com.simplyti.service.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8965190399057676169L;
	
	private final HttpResponseStatus status;
	
	public ServiceException(HttpResponseStatus status){
		this.status=status;
	}
	
	public HttpResponseStatus status(){
		return status;
	}

}
