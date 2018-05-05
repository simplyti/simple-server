package com.simplyti.service.api.builder.jaxrs;

import com.simplyti.service.api.APIContext;
import com.simplyti.service.api.ApiInvocationContext;

import io.netty.util.concurrent.Future;

public class JAXRSApiContext<O> implements APIContext<O>{
	
	private final ApiInvocationContext<Object, O> ctx;
	
	public JAXRSApiContext(ApiInvocationContext<Object, O> ctx){
		this.ctx=ctx;
	}
	
	public Future<Void> send(O response) {
		return ctx.send(response);
	}

	public Future<Void> failure(Throwable cause) {
		return ctx.failure(cause);
	}

}
