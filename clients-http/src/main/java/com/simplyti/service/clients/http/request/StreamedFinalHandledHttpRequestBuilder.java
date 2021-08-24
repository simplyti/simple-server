package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;

import com.simplyti.util.concurrent.Future;

public interface StreamedFinalHandledHttpRequestBuilder<T> {

	Future<Void> forEach(Consumer<T> object);
	
}
