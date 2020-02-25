package com.simplyti.server.http.api.handler;


import com.simplyti.server.http.api.request.ApiMatchRequest;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

public interface ApiInvocation {

	ApiMatchRequest match();
	HttpRequest request();
	HttpHeaders headers();
	
}
