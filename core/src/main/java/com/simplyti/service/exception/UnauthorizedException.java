package com.simplyti.service.exception;


import io.netty.handler.codec.http.HttpResponseStatus;

@Deprecated
public class UnauthorizedException extends ServiceException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4155329453662728437L;

	public UnauthorizedException() {
		super(HttpResponseStatus.UNAUTHORIZED);
	}

}
