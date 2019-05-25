package com.simplyti.service.api;

import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.api.builder.PathPattern;

import io.netty.handler.codec.http.HttpMethod;

public class ApiOperation<I,O> {
	
	private final HttpMethod method;
	private final PathPattern pathPattern;
	private final Consumer<ApiInvocationContext<I,O>> handler;
	private final TypeLiteral<I> requestType;
	private final int literalChars;
	private final boolean multipart;
	private final int maxBodyLength;
	private final Map<String,String> metadata;
	
	public ApiOperation(HttpMethod method, PathPattern pathPattern,
			Consumer<ApiInvocationContext<I,O>> handler, TypeLiteral<I> requestType, int literalChars,
			boolean multipart, int maxBodyLength, Map<String,String> metadata) {
		this.method=method;
		this.pathPattern=pathPattern;
		this.handler=handler;
		this.requestType=requestType;
		this.literalChars=literalChars;
		this.multipart=multipart;
		this.maxBodyLength=maxBodyLength;
		this.metadata=metadata;
	}
	
	public HttpMethod method() {
		return method;
	}

	public Pattern pathTemplate() {
		return pathPattern.pattern();
	}
	
	public PathPattern pathPattern() {
		return pathPattern;
	}

	public Consumer<ApiInvocationContext<I,O>> handler() {
		return handler;
	}

	public Map<String,Integer> pathParamNameToGroup() {
		return pathPattern.pathParamNameToGroup();
	}
	
	public final TypeLiteral<I> requestType(){
		return requestType;
	}

	public int literalChars() {
		return literalChars;
	}

	public boolean isMultipart() {
		return multipart;
	}

	public int maxBodyLength() {
		return maxBodyLength;
	}
	
	public Object meta(String name){
		return this.metadata.get(name);
	}

}
