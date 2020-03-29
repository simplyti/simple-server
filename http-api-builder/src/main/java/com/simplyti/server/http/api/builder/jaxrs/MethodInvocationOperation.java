package com.simplyti.server.http.api.builder.jaxrs;

import java.util.Collections;
import java.util.function.Consumer;

import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.operations.ApiOperation;
import com.simplyti.server.http.api.pattern.ApiPattern;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class MethodInvocationOperation<T> extends ApiOperation<JaxRsApiContext<T>> {
	
	private final TypeLiteral<?> requestType;

	public MethodInvocationOperation(HttpMethod method, ApiPattern pattern, TypeLiteral<?> requestType, Consumer<JaxRsApiContext<T>> consumer, ApiContextFactory factory) {
		super(method, pattern, Collections.emptyMap(), consumer, factory, false, false);
		this.requestType=requestType;
	}

}
