package com.simplyti.service.api;

import java.util.Collection;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;

public interface ApiResolver {

	ApiRequestInitializer getOperationFor(HttpMethod method, QueryStringDecoder queryStringDecoder);

	Collection<ApiOperation<?, ?, ?>> operations();

}
