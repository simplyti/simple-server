package com.simplyti.server.http.api.operations;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.base.MoreObjects;
import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.pattern.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ApiOperation<T extends ApiContext> {

	private final HttpMethod method;
	private final ApiPattern pattern;
	private final Consumer<T> handler;
	private final ApiContextFactory contextFactory;
	private final Map<String,Object> metadata;
	private final boolean streamedRequest;
	private final int maxBodyLength;

	public ApiOperation(HttpMethod method, ApiPattern pattern, Map<String,Object> metadata, Consumer<T> handler, ApiContextFactory contextFactory,
			boolean streamedRequest) {
		this(method, pattern, metadata, handler, contextFactory, streamedRequest, 10000000);
	}
	
	public ApiOperation(HttpMethod method, ApiPattern pattern, Map<String,Object> metadata, Consumer<T> handler, ApiContextFactory contextFactory,
			boolean streamedRequest,int maxBodyLength) {
		this.method=method;
		this.pattern=pattern;
		this.handler=handler;
		this.contextFactory=contextFactory;
		this.metadata=MoreObjects.firstNonNull(metadata, Collections.emptyMap());
		this.streamedRequest=streamedRequest;
		this.maxBodyLength=maxBodyLength;
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

}
