package com.simplyti.service.api;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

public interface ApiInvocation {

	ApiOperation<?, ?, ?> operation();
	HttpRequest request();
	HttpHeaders headers();
	
}
