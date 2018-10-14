package com.simplyti.service.api.builder.jaxrs;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.collect.Iterables;
import com.simplyti.service.api.ApiInvocationContext;
import com.simplyti.service.api.DefaultApiInvocationContext;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.util.concurrent.Future;

public class MethodInvocation implements Consumer<ApiInvocationContext<Object, Object>> {

	private final Map<Integer, RestParam> argumentIndexToRestParam;
	private final Method method;
	private final Object instance;
	private final Integer contexArgIndex;
	
	private final SyncTaskSubmitter syncTaskSubmitter;

	public MethodInvocation(Map<Integer, RestParam> argumentIndexToRestParam,Integer contexArgIndex, Method method,Object instance,
			SyncTaskSubmitter syncTaskSubmitter) {
		this.argumentIndexToRestParam=argumentIndexToRestParam;
		this.method=method;
		this.instance=instance;
		this.contexArgIndex=contexArgIndex;
		this.syncTaskSubmitter=syncTaskSubmitter;
	}

	@Override
	public void accept(ApiInvocationContext<Object, Object> ctx) {
		DefaultApiInvocationContext<?,?> def = (DefaultApiInvocationContext<?, ?>) ctx;
		Object[] args = new Object[method.getParameterCount()];
		for (Entry<Integer, RestParam> entry : argumentIndexToRestParam.entrySet()) {
			if (entry.getValue() instanceof RestPathParam) {
				RestPathParam pathParam = (RestPathParam) entry.getValue();
				args[entry.getKey()] = RestParam.convert(def.pathParam(pathParam.name()), pathParam.getType());
			} else if (entry.getValue() instanceof RestQueryParam) {
				RestQueryParam queryParam = (RestQueryParam) entry.getValue();
				resolveDefaultedNamedParam(args,entry,queryParam,def.queryParams(queryParam.name()));
			} else if (entry.getValue() instanceof RestHeaderParam) {
				RestHeaderParam headerParam = (RestHeaderParam) entry.getValue();
				resolveDefaultedNamedParam(args,entry,headerParam, def.request().headers().getAll(headerParam.name()));
			} else {
				args[entry.getKey()] = ctx.body();
			}
		}
		
		if(contexArgIndex!=null){
			args[contexArgIndex]=new JAXRSApiContext<>(ctx);
			try{
				method.invoke(instance, args);
			}catch(Throwable cause) {
				ctx.failure(cause);
			}
		}else{
			Future<Object> future = syncTaskSubmitter.submit(ctx.executor(),()->method.invoke(instance, args));
			future.addListener(f->{
				if(f.isSuccess()) {
					ctx.send(future.getNow());
				}else {
					ctx.failure(future.cause());
				}
			});
		}
	}

	private void resolveDefaultedNamedParam(Object[] args, Entry<Integer, RestParam> entry, NamedDefaultedRestParam param,List<String> values) {
		if (values!=null) {
			args[entry.getKey()] = resolvParam(param,values,entry.getValue().getType());
		} else {
			args[entry.getKey()] = param.defaultValue();
		}
	}
	
	private Object resolvParam(NamedDefaultedRestParam param, List<String> values, ResolvedType resolvedType) {
		if (param.getType().isInstanceOf(List.class) && values.size() != 1) {
			ResolvedType itemType = param.getType().typeParametersFor(List.class).get(0);
			return values.stream().map(value -> RestParam.convert(value, itemType)).collect(Collectors.toList());
		} else {
			return RestParam.convert(Iterables.getFirst(values, null), resolvedType);
		} 
	}

}
