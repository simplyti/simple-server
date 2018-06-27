package com.simplyti.service.api.builder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
	
	private Map<String, Set<String>> metadata;
	
	public FinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri, TypeLiteral<I> requestType, boolean multipart,
			int maxBodyLength) {
		this.builder=builder;
		this.method=method;
		this.uri=uri;
		this.requestType=requestType;
		this.multipart=multipart;
		this.maxBodyLength=maxBodyLength;
	}
	
	public FinishableApiBuilder<I,O> withMeta(String name, String... values) {
		if(metadata ==null) {
			metadata = Maps.newHashMap();
		}
		if(metadata.containsKey(name)) {
			Stream.of(values).forEach(value->metadata.get(name).add(value));
		}else {
			Set<String> list = new HashSet<>();
			Stream.of(values).forEach(value->list.add(value));
			metadata.put(name, list);
		}
		return this;
	}
	
	public void then(Consumer<ApiInvocationContext<I,O>> consumer) {
		PathPattern pathPattern = PathPattern.build(uri);
		builder.add(new ApiOperation<I,O>(method, pathPattern.pattern(), pathPattern.pathParamNameToGroup(),consumer,requestType,pathPattern.literalCount(),
				multipart,noNegative(maxBodyLength,DEFAULT_MAX_BODY),metadata()));
	}
	
	private Map<String,Set<String>> metadata() {
		if(metadata==null) {
			return Collections.emptyMap();
		}else {
			Supplier<ImmutableMap.Builder<String, Set<String>>> supplier = ImmutableMap.Builder::new;
			return this.metadata.entrySet().stream()
				.collect(Collector.of(supplier, 
						(b, entry) -> b.put(entry.getKey(),Collections.unmodifiableSet(entry.getValue())), 
						(l, r) -> l.putAll(r.build()), 
						ImmutableMap.Builder::build));
		}
	}

	private int noNegative(int value,int defaultValue) {
		if(value<0) {
			return defaultValue;
		}else {
			return value;
		}
	}
	

}
