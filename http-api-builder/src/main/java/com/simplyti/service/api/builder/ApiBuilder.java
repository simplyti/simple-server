package com.simplyti.service.api.builder;

import com.simplyti.server.http.api.builder.HttpMethodApiBuilder;

public interface ApiBuilder {
	
	HttpMethodApiBuilder when();

	void usingJaxRSContract(Class<?> clazz);

}
