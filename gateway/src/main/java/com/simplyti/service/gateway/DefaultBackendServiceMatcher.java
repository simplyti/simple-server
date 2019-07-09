package com.simplyti.service.gateway;

import java.util.regex.Matcher;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.internal.StringUtil;

public class DefaultBackendServiceMatcher implements BackendServiceMatcher {

	private final Matcher matcher;
	private final BackendService service;
	private final String query;

	public DefaultBackendServiceMatcher(BackendService service, String uri) {
		this.service=service;
		if(service.pattern()!=null) {
			String path;
			int queryDelimiter = uri.indexOf('?');
			if(queryDelimiter==-1) {
				path=uri;
				query=StringUtil.EMPTY_STRING;
			}else {
				path = uri.substring(0,queryDelimiter);
				query=uri.substring(queryDelimiter);
			}
			this.matcher=service.pattern().matcher(path);
		}else {
			this.matcher=null;
			this.query=null;
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
			final String newpath = service.rewrite().doRewrite(matcher, service.pathPattern());
			return request.setUri(newpath+query);
		}else {
			String root;
			String resource;
			if(request.uri().equals("/")) {
				root = service.rewrite().rewrite();
				resource = "";
			}else {
				root = service.rewrite().rewrite().replaceAll("/$", StringUtil.EMPTY_STRING);
				resource =request.uri();
			}
			return request.setUri(root+resource);
		}
	}
	
}
