package com.simplyti.service.clients.http.request;

import io.netty.handler.codec.http.HttpContent;
import io.netty.util.concurrent.Future;

public interface StreamedHttpRequest{
	
	boolean isDone();

	boolean isSuccess();

	Future<Void> send(HttpContent request);

}
