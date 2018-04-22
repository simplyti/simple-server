package com.simplyti.service.api.builder.jaxrs;

import com.simplyti.service.api.ApiInvocationContext;

public class JAXRSApiContext<O> {
	
	private final ApiInvocationContext<Object, O> ctx;
	
	public JAXRSApiContext(ApiInvocationContext<Object, O> ctx){
		this.ctx=ctx;
	}
	
	public void send(O response) {
		ctx.send(response);
	}

	public void failure(Throwable cause) {
		ctx.failure(cause);
	}

}
