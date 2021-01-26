package com.simplyti.server.http.api.builder.jaxrs;

import java.lang.reflect.Method;
import java.util.Map;

import com.simplyti.service.matcher.jaxrs.RestParam;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.util.concurrent.Future;

public class JaxRsBlockingMethodInvocation<T> extends JaxRsMethodInvocation<T> {
	
	private final Object instance;
	private final Method method;
	private final SyncTaskSubmitter syncTaskSubmitter;
	
	public JaxRsBlockingMethodInvocation(Object instance, Method method, Map<Integer, RestParam> parameters, SyncTaskSubmitter syncTaskSubmitter) {
		super(method, parameters);
		this.instance=instance;
		this.method=method;
		this.syncTaskSubmitter=syncTaskSubmitter;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void accept0(JaxRsApiContext<T> ctx, Object[] args) {
		Future<Object> future = syncTaskSubmitter.submit(ctx.executor(),()->method.invoke(instance, args));
		future.thenAccept(obj->ctx.writeAndFlush((T) obj))
			.onError(ctx::failure);
	}

}
