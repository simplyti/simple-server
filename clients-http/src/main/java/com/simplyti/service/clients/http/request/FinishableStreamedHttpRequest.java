package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;

public interface FinishableStreamedHttpRequest {
	
	StreamedHttpRequest forEach(Consumer<Object> consumer);
	
}
