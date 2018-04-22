package com.simplyti.service.exception;


import io.netty.handler.codec.http.HttpResponseStatus;

public class NotFoundException extends ServiceException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4155329453662728437L;

	public NotFoundException() {
		super(HttpResponseStatus.NOT_FOUND);
	}

}
