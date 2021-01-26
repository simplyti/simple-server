package com.simplyti.server.http.api.operations;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.service.matcher.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ApiOperation<T extends ApiContext> {

	private static final int DEFAULT_MAX_BODY = 10000000;
	
	private final HttpMethod method;
	private final ApiPattern pattern;
	private final Consumer<T> handler;
	private final ApiContextFactory contextFactory;
	private final Map<String,Object> metadata;
	private final boolean streamedRequest;
	private final int maxBodyLength;
	private final boolean notFoundOnNull;

	public ApiOperation(HttpMethod method, ApiPattern pattern, Map<String,Object> metadata, Consumer<T> handler, ApiContextFactory contextFactory,
			boolean streamedRequest, boolean notFoundOnNull) {
		this(method, pattern, metadata, handler, contextFactory, streamedRequest, notFoundOnNull, 0);
	}
	
	public ApiOperation(HttpMethod method, ApiPattern pattern, Map<String,Object> metadata, Consumer<T> handler, ApiContextFactory contextFactory,
			boolean streamedRequest, boolean notFoundOnNull, int maxBodyLength) {
		this.method=method;
		this.pattern=pattern;
		this.handler=handler;
		this.contextFactory=contextFactory;
		this.metadata=firstNonNull(metadata, Collections.emptyMap());
		this.streamedRequest=streamedRequest;
		this.maxBodyLength=maxBodyLength>0?maxBodyLength:DEFAULT_MAX_BODY;
		this.notFoundOnNull=notFoundOnNull;
	}

	@SuppressWarnings("unchecked")
	public <U> U meta(String key) {
		return (U) metadata.get(key);
	}

	public Map<String, Integer> pathParamNameToGroup() {
		return pattern.pathParamNameToGroup();
	}
	
	public boolean isStreamed() {
		return this.streamedRequest;
	}
	
	private static <Q> Q firstNonNull(Q obj1, Q obj2) {
		if(obj1 !=null) {
			return obj1;
		} 
		return obj2;
	}

}
