package com.simplyti.server.http.api.operations;

import java.util.Map;

import com.simplyti.server.http.api.builder.RequestResponseTypedApiContextConsumer;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.context.RequestResponseTypedApiContext;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.matcher.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class RequestResponseTypeApiOperation<T,U> extends ApiOperation<RequestResponseTypedApiContext<T,U>> {
	
	private final TypeLiteral<T> requestType;

	public RequestResponseTypeApiOperation(HttpMethod method, ApiPattern pattern, Map<String,Object> metadata, TypeLiteral<T> requestType, RequestResponseTypedApiContextConsumer<T,U> consumer, 
			ApiContextFactory contextFactory, boolean notFoundOnNull) {
		super(method, pattern,metadata,consumer,contextFactory,false, notFoundOnNull);
		this.requestType=requestType;
	}
	
}