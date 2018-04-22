package com.simplyti.service.api.builder;

import io.netty.handler.codec.http.HttpMethod;

public class MethodApiBuilder {

	private final ApiBuilder builder;

	public MethodApiBuilder(ApiBuilder builder) {
		this.builder=builder;
	}

	public RawFinishableApiBuilder get(String uri) {
		return new RawFinishableApiBuilder(builder,HttpMethod.GET,uri);
	}

	public TypeableRequestFinishableApiBuilder post(String uri) {
		return new TypeableRequestFinishableApiBuilder(builder,HttpMethod.POST,uri);
	}

	public TypeableRequestFinishableApiBuilder delete(String uri) {
		return new TypeableRequestFinishableApiBuilder(builder,HttpMethod.DELETE,uri);
	}

}
