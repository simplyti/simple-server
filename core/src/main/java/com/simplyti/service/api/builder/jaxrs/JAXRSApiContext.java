package com.simplyti.service.api.builder.jaxrs;

import com.simplyti.service.api.APIContext;
import com.simplyti.service.api.ApiInvocationContext;

import lombok.experimental.Delegate;

public class JAXRSApiContext<O> implements APIContext<O>{
	
	@Delegate
	private final ApiInvocationContext<Object, O> ctx;
	
	public JAXRSApiContext(ApiInvocationContext<Object, O> ctx){
		this.ctx=ctx;
	}

}
