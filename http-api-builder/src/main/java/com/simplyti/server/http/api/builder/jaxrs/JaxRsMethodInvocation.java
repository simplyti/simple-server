package com.simplyti.server.http.api.builder.jaxrs;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.fasterxml.classmate.ResolvedType;
import com.simplyti.service.matcher.jaxrs.NamedDefaultedRestParam;
import com.simplyti.service.matcher.jaxrs.RestHeaderParam;
import com.simplyti.service.matcher.jaxrs.RestParam;
import com.simplyti.service.matcher.jaxrs.RestPathParam;
import com.simplyti.service.matcher.jaxrs.RestQueryParam;

public abstract class JaxRsMethodInvocation<T> implements Consumer<JaxRsApiContext<T>>{
	
	private final Method method;
	private final Map<Integer, RestParam> parameters;

	public JaxRsMethodInvocation(Method method, Map<Integer,RestParam> parameters) {
		this.method=method;
		this.parameters=parameters;
	}

	@Override
	public void accept(JaxRsApiContext<T> ctx) {
		Object[] args = new Object[method.getParameterCount()];
		for (Entry<Integer, RestParam> entry : parameters.entrySet()) {
			if (entry.getValue() instanceof RestPathParam) {
				RestPathParam pathParam = (RestPathParam) entry.getValue();
				args[entry.getKey()] = RestParam.convert(ctx.pathParam(pathParam.name()), pathParam.getType());
			} else if (entry.getValue() instanceof RestQueryParam) {
				RestQueryParam queryParam = (RestQueryParam) entry.getValue();
				resolveDefaultedNamedParam(args,entry,queryParam,ctx.queryParams(queryParam.name()));
			} else if (entry.getValue() instanceof RestHeaderParam) {
				RestHeaderParam headerParam = (RestHeaderParam) entry.getValue();
				resolveDefaultedNamedParam(args,entry,headerParam, ctx.request().headers().getAll(headerParam.name()));
			} else {
				args[entry.getKey()] = ctx.body();
			}
		}
		
		accept0(ctx, args);
	}
	
	protected abstract void accept0(JaxRsApiContext<T> ctx, Object[] args);

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
		} else if(values.size()>0){
			return RestParam.convert(values.get(0), resolvedType);
		} else {
			return null;
		}
	}



}
