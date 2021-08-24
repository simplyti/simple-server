package com.simplyti.server.http.api.context;

import com.simplyti.util.concurrent.Future;

public interface ResponseTypedApiContext<T> extends ApiContext {

	Future<Void> writeAndFlush(T value);
	
	Future<Void> send(T value);

}
