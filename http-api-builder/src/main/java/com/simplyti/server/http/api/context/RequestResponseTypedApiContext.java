package com.simplyti.server.http.api.context;

import com.simplyti.util.concurrent.Future;

public interface RequestResponseTypedApiContext<T,U> extends ApiContext {
	
	T body();

	Future<Void> writeAndFlush(U body);

	Future<Void> send(U body);

}
