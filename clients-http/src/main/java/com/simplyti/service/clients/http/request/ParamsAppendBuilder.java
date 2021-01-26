package com.simplyti.service.clients.http.request;

import java.util.HashMap;
import java.util.Map;

import io.netty.handler.codec.http.QueryStringEncoder;

public class ParamsAppendBuilder<T> implements ParamAppendableRequestBuilder<T>{
	
	private final T parent;
	
	private Map<String,Object> params;

	public ParamsAppendBuilder(Map<String,Object> params,T parent) {
		this.parent=parent;
		this.params=params;
	}

	@Override
	public T params(Map<String, String> params) {
		initializeParams();
		this.params.putAll(params);
		return parent;
	}
	
	@Override
	public T param(String name, Object value) {
		initializeParams();
		this.params.put(name, value);
		return parent;
	}

	@Override
	public T param(String name) {
		initializeParams();
		this.params.put(name, null);
		return parent;
	}
	
	private void initializeParams() {
		if(params==null) {
			this.params=new HashMap<>();
		}
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public String withParams(String uri) {
		if(params == null) {
			return uri;
		}
		QueryStringEncoder encoder = new QueryStringEncoder(uri);
		params.forEach((name,value)->encoder.addParam(name, value!=null?value.toString():null));
		return encoder.toString();
	}

}
