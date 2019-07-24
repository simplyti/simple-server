package com.simplyti.service.api.builder;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.simplyti.service.api.ApiOperation;
import com.simplyti.service.api.StreamedApiInvocationContext;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

public class TypedResponseStreamedApiBuilder<O> {

	private static final TypeLiteral<Void> VOID = TypeLiteral.create(Void.class);
	
	private final ApiBuilder builder;
	private final HttpMethod method;
	private final String uri;
	
	private Map<String, String> metadata;

	public TypedResponseStreamedApiBuilder(ApiBuilder builder, HttpMethod method, String uri) {
		this.builder=builder;
		this.method=method;
		this.uri=uri;
	}

	public void then(Consumer<StreamedApiInvocationContext<O>> consumer) {
		PathPattern pathPattern = PathPattern.build(uri);
		builder.add(new ApiOperation<Void,O,StreamedApiInvocationContext<O>>(method, pathPattern,consumer,VOID,pathPattern.literalCount(),
				false,0,metadata(),true));
	}
	
	public TypedResponseStreamedApiBuilder<O> withMeta(String name, String value) {
		if(metadata ==null) {
			metadata = new HashMap<>();
		}
		metadata.put(name, value);
		return this;
	}
	
	private Map<String,String> metadata() {
		if(metadata==null) {
			return Collections.emptyMap();
		}else {
			return new HashMap<>(metadata);
		}
	}

}
