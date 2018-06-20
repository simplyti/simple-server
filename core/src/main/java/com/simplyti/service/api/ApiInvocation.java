package com.simplyti.service.api;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.google.common.collect.Maps;

import io.netty.buffer.DefaultByteBufHolder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpUtil;

public class ApiInvocation<I> extends DefaultByteBufHolder {
	
	private final ApiOperation<I,?> operation;
	private final Matcher matcher;
	private final Map<String, List<String>> params;
	private final boolean keepAlive;
	private final HttpHeaders headers;
	private final FullHttpRequest request;
	
	private final Map<String,String> cachedpathParams = Maps.newHashMap();
	
	public ApiInvocation(ApiOperation<I,?> operation,Matcher matcher,Map<String, List<String>> params, FullHttpRequest request) {
		super(request.content().retain());
		this.headers=request.headers();
		this.request=request;
		this.operation=operation;
		this.matcher=matcher;
		this.params=params;
		this.keepAlive=HttpUtil.isKeepAlive(request);
	}

	public ApiOperation<I,?> operation() {
		return operation;
	}

	public Matcher matcher() {
		return matcher;
	}

	public Map<String, List<String>> params() {
		return params;
	}
	
	public boolean isKeepAlive() {
		return keepAlive;
	}

	public HttpHeaders headers() {
		return headers;
	}

	public FullHttpRequest request() {
		return request;
	}
	
	public String pathParam(String key) {
		return cachedpathParams.computeIfAbsent(key, theKey->{
			Integer group = operation.pathParamNameToGroup().get(key);
			if(group==null){
				return null;
			}else{
				return matcher.group(group);
			}
		});
	}

}
