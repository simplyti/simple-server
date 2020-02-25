package com.simplyti.server.http.api.operations;

import java.util.Map;

import com.simplyti.server.http.api.builder.RequestTypedApiContextConsumer;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.context.RequestTypedApiContext;
import com.simplyti.server.http.api.pattern.ApiPattern;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class RequestTypeApiOperation<T> extends ApiOperation<RequestTypedApiContext<T>> {
	
	private final TypeLiteral<T> requestType;

	public RequestTypeApiOperation(HttpMethod method, ApiPattern pattern,Map<String,Object> metadata, TypeLiteral<T> requestType, RequestTypedApiContextConsumer<T> consumer,
			ApiContextFactory contextFactory) {
		super(method, pattern,metadata,consumer,contextFactory,false);
		this.requestType=requestType;
	}
	
}