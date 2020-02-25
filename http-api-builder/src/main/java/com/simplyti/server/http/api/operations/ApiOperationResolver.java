package com.simplyti.server.http.api.operations;

import com.simplyti.server.http.api.request.ApiMatchRequest;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;

public interface ApiOperationResolver {

	ApiMatchRequest resolveOperation(HttpMethod method, String path, QueryStringDecoder queryDecoder);
	
}
