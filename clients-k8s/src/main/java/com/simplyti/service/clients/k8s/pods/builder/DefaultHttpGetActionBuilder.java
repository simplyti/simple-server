package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.HttpGetActionHookHandler;
import com.simplyti.service.clients.k8s.pods.domain.HttpHeader;
import com.simplyti.service.clients.k8s.pods.domain.LifecycleHookHandler;

import java.util.ArrayList;
import java.util.List;

public class DefaultHttpGetActionBuilder<T> implements HttpGetActionBuilder<T> {

    private final LifecycleBuilder<T> parent;
    private final LifecycleHandlerHandler handler;
    
    private String scheme;
    private String host;
    private Object port;
    private String path;
    private List<HttpHeader> headers;

    public DefaultHttpGetActionBuilder(final LifecycleBuilder<T> parent, final LifecycleHandlerHandler handler) {
        this.parent = parent;
        this.handler = handler;
    }
    
    @Override
	public HttpGetActionBuilder<T> scheme(String scheme) {
		this.scheme=scheme;
		return this;
	}

    @Override
    public HttpGetActionBuilder<T> host(String host) {
        this.host = host;
        return this;
    }
    
    @Override
	public HttpGetActionBuilder<T> port(Object port) {
		this.port=port;
		return this;
	}
    
    @Override
	public HttpGetActionBuilder<T> path(String path) {
    	this.path=path;
		return this;
	}

    @Override
    public HttpGetActionBuilder<T> header(String name, String value) {
    	if(this.headers==null) {
    		this.headers=new ArrayList<>();
    	}
        this.headers.add(new HttpHeader(name, value));
        return null;
    }

    @Override
    public LifecycleBuilder<T> build() {
        this.handler.setHookHandler(new LifecycleHookHandler(null,new HttpGetActionHookHandler(scheme, host, port, path,headers),null));
        return this.parent;
    }

}
