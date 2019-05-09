package com.simplyti.service.gateway;

import java.util.Map;
import java.util.regex.Matcher;

import com.google.common.collect.Iterables;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.internal.StringUtil;

public class DefaultBackendServiceMatcher implements BackendServiceMatcher {

	private final Matcher matcher;
	private final BackendService service;

	public DefaultBackendServiceMatcher(BackendService service, String path) {
		this.service=service;
		if(service.pattern()!=null) {
			this.matcher=service.pattern().matcher(path);
		}else {
			this.matcher=null;
		}
	}

	@Override
	public boolean matches() {
		return matcher==null || matcher.matches();
	}

	@Override
	public BackendService get() {
		return service;
	}

	@Override
	public HttpRequest rewrite(HttpRequest request) {
		if(service.rewrite()==null) {
			return request;
		} else if(service.pathPattern()!=null || service.pattern()!=null) {
			final String newpath;
			if(service.pathPattern()!=null) {
				Map<String, Integer> pathToGroup = service.pathPattern().pathParamNameToGroup();
				newpath=service.rewrite()+"/"+this.matcher.group(Iterables.get(pathToGroup.values(), 0));
			}else {
				newpath=service.rewrite().replaceAll("/$", StringUtil.EMPTY_STRING)+"/"+this.matcher.group(1);
			}
			return request.setUri(newpath);
		}else {
			String root;
			String resource;
			if(request.uri().equals("/")) {
				root = service.rewrite();
				resource = "";
			}else {
				root = service.rewrite().replaceAll("/$", StringUtil.EMPTY_STRING);
				resource =request.uri();
			}
			return request.setUri(root+resource);
		}
	}
	
}
