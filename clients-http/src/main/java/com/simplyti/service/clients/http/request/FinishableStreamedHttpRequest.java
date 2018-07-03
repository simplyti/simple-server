package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;

import io.netty.handler.codec.http.HttpObject;

public interface FinishableStreamedHttpRequest {
	
	StreamedHttpRequest forEach(Consumer<HttpObject> consumer);
	
}
