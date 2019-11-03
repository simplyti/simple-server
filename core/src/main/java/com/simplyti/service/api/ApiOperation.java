package com.simplyti.service.api;

import java.util.Map;
import java.util.function.Consumer;

import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.google.re2j.Pattern;
import com.simplyti.service.api.builder.PathPattern;

import io.netty.handler.codec.http.HttpMethod;

public class ApiOperation<I,O,C extends APIContext<O>> {
	
	private final HttpMethod method;
	private final PathPattern pathPattern;
	private final Consumer<C> handler;
	private final TypeLiteral<I> requestType;
	private final int literalChars;
	private final boolean multipart;
	private final int maxBodyLength;
	private final Map<String,String> metadata;
	private final boolean streamedRequest;
	private final boolean notFoundOnNull;
	
	public ApiOperation(HttpMethod method, PathPattern pathPattern,
			Consumer<C> handler, TypeLiteral<I> requestType, int literalChars,
			boolean multipart, int maxBodyLength, Map<String,String> metadata,
			boolean streamedRequest, boolean notFoundOnNull) {
		this.method=method;
		this.pathPattern=pathPattern;
		this.handler=handler;
		this.requestType=requestType;
		this.literalChars=literalChars;
		this.multipart=multipart;
		this.maxBodyLength=maxBodyLength;
		this.metadata=metadata;
		this.streamedRequest=streamedRequest;
		this.notFoundOnNull=notFoundOnNull;
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

	public Consumer<C> handler() {
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

	public boolean isStreamed() {
		return this.streamedRequest;
	}

	public boolean notFoundOnNull() {
		return notFoundOnNull;
	}

}
