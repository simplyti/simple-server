package com.simplyti.server.http.api.builder.jaxrs;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.util.concurrent.Future;

public interface JaxRsApiContext<T> extends ApiContext {
	
	public Future<Void> writeAndFlush(T value);

	public Future<Void> send(T value);

	public Object body();

}
