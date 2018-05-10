package com.simplyti.service.api.builder.jaxrs;

import com.simplyti.service.api.APIContext;

import lombok.experimental.Delegate;

public class JAXRSApiContext<O> implements APIContext<O>{
	
	@Delegate
	private final APIContext<O> ctx;
	
	public JAXRSApiContext(APIContext<O> ctx){
		this.ctx=ctx;
	}

}
