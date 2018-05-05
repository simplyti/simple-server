package com.simplyti.service.api;

import io.netty.util.concurrent.Future;

public interface APIContext<T> {
	
	public Future<Void> send(T response);
	public Future<Void> failure(Throwable error);

}
