package com.simplyti.server.http.api.builder.jaxrs;

import java.util.Collections;

import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.operations.ApiOperation;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.matcher.ApiPattern;
import com.simplyti.util.concurrent.ThrowableConsumer;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class MethodInvocationOperation<T> extends ApiOperation<JaxRsApiContext<T>> {
	
	private final TypeLiteral<?> requestType;

	public MethodInvocationOperation(HttpMethod method, ApiPattern pattern, TypeLiteral<?> requestType, ThrowableConsumer<JaxRsApiContext<T>> consumer, ApiContextFactory factory) {
		super(method, pattern, Collections.emptyMap(), consumer, factory, false, false);
		this.requestType=requestType;
	}

}
