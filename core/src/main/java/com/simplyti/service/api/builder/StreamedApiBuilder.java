package com.simplyti.service.api.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.api.ApiOperation;
import com.simplyti.service.api.StreamedApiInvocationContext;

import io.netty.handler.codec.http.HttpMethod;

public class StreamedApiBuilder {
	
	private static final TypeLiteral<Void> VOID = null;
	
	private final ApiBuilder builder;
	private final HttpMethod method;
	private final String uri;
	
	private Map<String, String> metadata;

	public StreamedApiBuilder(ApiBuilder builder, HttpMethod method, String uri) {
		this.builder=builder;
		this.method=method;
		this.uri=uri;
	}

	public void then(Consumer<StreamedApiInvocationContext<Object>> consumer) {
		PathPattern pathPattern = PathPattern.build(uri);
		builder.add(new ApiOperation<Void,Object,StreamedApiInvocationContext<Object>>(method, pathPattern,consumer,VOID,pathPattern.literalCount(),
				false,0,metadata(),true));
	}

	public <O> TypedResponseStreamedApiBuilder<O> withResponseBodyType(Class<O> responseType) {
		return new TypedResponseStreamedApiBuilder<>(builder,method,uri);
	}
	
	private Map<String,String> metadata() {
		if(metadata==null) {
			return Collections.emptyMap();
		}else {
			return new HashMap<>(metadata);
		}
	}

}
