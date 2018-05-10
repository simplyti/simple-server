package com.simplyti.service.api.builder;

import java.util.function.Consumer;

import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.api.ApiInvocationContext;
import com.simplyti.service.api.ApiOperation;

import io.netty.handler.codec.http.HttpMethod;

public abstract class FinishableApiBuilder<I,O> {
	
	private static final int DEFAULT_MAX_BODY = 10000000;
	
	protected final ApiBuilder builder;
	protected final HttpMethod method;
	protected final String uri;
	protected final TypeLiteral<I> requestType;
	protected final boolean multipart;
	
	
	protected int maxBodyLength;
	private boolean requiresAuth;
	
	
	public FinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri, TypeLiteral<I> requestType, boolean multipart,
			int maxBodyLength) {
		this.builder=builder;
		this.method=method;
		this.uri=uri;
		this.requestType=requestType;
		this.multipart=multipart;
		this.maxBodyLength=maxBodyLength;
	}
	
	public FinishableApiBuilder<I,O> withRequiresAuth() {
		this.requiresAuth=true;
		return this;
	}
	
	public void then(Consumer<ApiInvocationContext<I,O>> consumer) {
		PathPattern pathPattern = PathPattern.build(uri);
		builder.add(new ApiOperation<I,O>(method, pathPattern.pattern(), requiresAuth, pathPattern.pathParamNameToGroup(),consumer,requestType,pathPattern.literalCount(),
				multipart,noNegative(maxBodyLength,DEFAULT_MAX_BODY)));
	}
	
	private int noNegative(int value,int defaultValue) {
		if(value<0) {
			return defaultValue;
		}else {
			return value;
		}
	}
	

}
