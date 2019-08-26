package com.simplyti.service.api;

import java.util.Collection;
import java.util.Optional;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;

public interface ApiResolver {

	Optional<ApiMacher> getOperationFor(HttpMethod method, QueryStringDecoder queryStringDecoder);

	Collection<ApiOperation<?, ?, ?>> operations();

}
