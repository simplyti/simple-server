package com.simplyti.service.api.builder.jaxrs;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.collect.Iterables;
import com.simplyti.service.api.ApiInvocationContext;
import com.simplyti.service.api.DefaultApiInvocationContext;

public class MethodInvocation implements Consumer<ApiInvocationContext<Object, Object>> {

	private final Map<Integer, RestParam> argumentIndexToRestParam;
	private final Method method;
	private final Object instance;
	private final Integer contexArgIndex;
	
	private final ExecutorService blockingExecutor;

	public MethodInvocation(Map<Integer, RestParam> argumentIndexToRestParam,Integer contexArgIndex, Method method,Object instance,
			ExecutorService blockingExecutor) {
		this.argumentIndexToRestParam=argumentIndexToRestParam;
		this.method=method;
		this.instance=instance;
		this.contexArgIndex=contexArgIndex;
		this.blockingExecutor=blockingExecutor;
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
				List<String> params = def.queryParams(queryParam.name());
				if (params!=null) {
					args[entry.getKey()] = resolvQueryParam(queryParam,params,entry.getValue().getType());
				} else {
					args[entry.getKey()] = queryParam.defaultValue();
				}
			} else {
				args[entry.getKey()] = ctx.body();
			}
		}
		
		
		if(contexArgIndex!=null){
			args[contexArgIndex]=new JAXRSApiContext<>(ctx);
			try{
				method.invoke(instance, args);
			}catch(Throwable error){
				handleMethodException(ctx,error);
			}
		}else{
			blockingExecutor.execute(()->handleBlocking(ctx, args));
		}
	}

	private void handleMethodException(ApiInvocationContext<Object, Object> ctx, Throwable error) {
		ctx.failure(error.getCause());
	}

	private void handleBlocking(ApiInvocationContext<Object, Object> ctx,Object[] args) {
		try{
			ctx.send(method.invoke(instance, args));
		}catch (Throwable error){
			handleMethodException(ctx,error);
		}
	}

	private Object resolvQueryParam(RestQueryParam queryParam, List<String> params, ResolvedType resolvedType) {
		if (queryParam.getType().isInstanceOf(List.class) && params.size() != 1) {
			ResolvedType itemType = queryParam.getType().typeParametersFor(List.class).get(0);
			return params.stream().map(param -> RestParam.convert(param, itemType)).collect(Collectors.toList());
		} else {
			return RestParam.convert(Iterables.getFirst(params, null), resolvedType);
		} 
	}

}
