package com.simplyti.server.http.api.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.concurrent.EventExecutor;

public abstract class AbstractApiContext implements ApiContext {
	
	private static final String STRING = "string";
	
	private final SyncTaskSubmitter syncTaskSubmitter;
	private final Channel channel;
	private final HttpRequest request;
	private final ApiMatchRequest matcher;
	
	private final Map<String,Object> convertedPathParams = new HashMap<>();
	private final Map<String,Object> convertedQueryParams = new HashMap<>();


	public AbstractApiContext(SyncTaskSubmitter syncTaskSubmitter, Channel channel, HttpRequest request, ApiMatchRequest matcher) {
		this.syncTaskSubmitter=syncTaskSubmitter;
		this.channel=channel;
		this.request=request;
		this.matcher=matcher;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T queryParam(String name, String type, Function<String,T> fn) {
		return (T) this.convertedQueryParams.computeIfAbsent(String.format("%s.%s", type,name), key->{
			if(this.matcher.parameters().containsKey(name)) {
				List<String> params = this.matcher.parameters().get(name);
				if(params.isEmpty()) {
					return null;
				}else {
					return fn.apply(params.get(0));
				}
			}else {
				return null;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private <T> T pathParam(String key, String type, Function<String,T> fn) {
		return (T) convertedPathParams.computeIfAbsent(String.format("%s.%s", type,key), theKey->{
			Integer group = matcher.operation().pathParamNameToGroup().get(key);
			if(group==null){
				return null;
			}else{
				return fn.apply(matcher.group(group));
			}
		});
	}
	
	@Override
	public String queryParam(String name) {
		return queryParam(name,STRING,Function.identity());
	}
	
	@Override
	public Channel channel() {
		return channel;
	}
	
	@Override
	public EventExecutor executor() {
		return channel.eventLoop();
	}

	@Override
	public List<String> queryParams(String name) {
		return this.matcher.parameters().get(name);
	}

	@Override
	public Map<String,List<String>> queryParams() {
		return this.matcher.parameters();
	}

	@Override
	public String pathParam(String key) {
		return pathParam(key,STRING,Function.identity());
	}

	@Override
	public HttpRequest request() {
		return request;
	}
	
	@Override
	public <T> Future<T> sync(Callable<T> task) {
		return syncTaskSubmitter.submit(executor(), task);
	}

}
