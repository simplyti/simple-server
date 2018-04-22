package com.simplyti.service.api;

import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.jsoniter.spi.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiOperation<I,O> {
	
	private final HttpMethod method;
	private final Pattern pathTemplate;
	private final Map<String,Integer> pathParamNameToGroup;
	private final Consumer<ApiInvocationContext<I,O>> handler;
	private final TypeLiteral<I> requestType;
	private final int literalChars;
	private final boolean multipart;
	
	public HttpMethod method() {
		return method;
	}

	public Pattern pathTemplate() {
		return pathTemplate;
	}

	public Consumer<ApiInvocationContext<I,O>> handler() {
		return handler;
	}

	public Map<String,Integer> pathParamNameToGroup() {
		return pathParamNameToGroup;
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
	
}
