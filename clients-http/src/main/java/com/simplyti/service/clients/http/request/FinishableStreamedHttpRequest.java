package com.simplyti.service.clients.http.request;

import io.netty.handler.codec.http.HttpObject;
import io.netty.util.concurrent.Future;

public interface FinishableStreamedHttpRequest{
	
	Future<Void> send(HttpObject request);

	boolean isDone();

	boolean isSuccess();

}
