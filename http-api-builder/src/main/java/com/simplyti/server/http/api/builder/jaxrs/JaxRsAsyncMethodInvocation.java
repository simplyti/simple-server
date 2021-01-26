package com.simplyti.server.http.api.builder.jaxrs;

import java.lang.reflect.Method;
import java.util.Map;

import com.simplyti.service.matcher.jaxrs.RestParam;

public class JaxRsAsyncMethodInvocation<T> extends JaxRsMethodInvocation<T> {
	
	private final Object instance;
	private final Method method;
	private final int contexArgIndex;
	
	public JaxRsAsyncMethodInvocation(Object instance, Method method, Map<Integer, RestParam> parameters, int contexArgIndex) {
		super(method, parameters);
		this.instance=instance;
		this.method=method;
		this.contexArgIndex=contexArgIndex;
	}

	@Override
	protected void accept0(JaxRsApiContext<T> ctx, Object[] args) {
		args[contexArgIndex] = ctx;
		try{
			method.invoke(instance, args);
		}catch(Throwable cause) {
			ctx.failure(cause);
		}

	}

}
