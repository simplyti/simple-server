package com.simplyti.service.api.builder;

import java.util.function.Consumer;

import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.api.ApiInvocationContext;
import com.simplyti.service.api.ApiOperation;

import io.netty.handler.codec.http.HttpMethod;

public abstract class FinishableApiBuilder<I,O> {
	
	protected final ApiBuilder builder;
	protected final HttpMethod method;
	protected final String uri;
	protected final TypeLiteral<I> requestType;
	protected final boolean multipart;
	
	public FinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri, TypeLiteral<I> requestType, boolean multipart) {
		this.builder=builder;
		this.method=method;
		this.uri=uri;
		this.requestType=requestType;
		this.multipart=multipart;
	}
	
	public void then(Consumer<ApiInvocationContext<I,O>> consumer) {
		PathPattern pathPattern = PathPattern.build(uri);
		builder.add(new ApiOperation<I,O>(method, pathPattern.pattern(),pathPattern.pathParamNameToGroup(),consumer,requestType,pathPattern.literalCount(),multipart));
	}
	

}
