package com.simplyti.server.http.api.builder.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.pattern.ApiPattern;
import com.simplyti.service.api.builder.di.InstanceProvider;
import com.simplyti.service.api.builder.jaxrs.RestBodyParam;
import com.simplyti.service.api.builder.jaxrs.RestHeaderParam;
import com.simplyti.service.api.builder.jaxrs.RestParam;
import com.simplyti.service.api.builder.jaxrs.RestPathParam;
import com.simplyti.service.api.builder.jaxrs.RestQueryParam;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.util.internal.StringUtil;

public class JaxRSBuilder {
	
	private static final TypeLiteral<Object> VOID_TYPE = TypeLiteral.create(Void.class);

	public static void build(Class<?> clazz, InstanceProvider instanceProvider, SyncTaskSubmitter syncTaskSubmitter, ApiOperations operations,
			ApiContextFactory factory) {
		Stream.of(clazz.getDeclaredMethods())
			.filter(method -> Stream.of(method.getAnnotations()).anyMatch(ann -> ann.annotationType().isAnnotationPresent(HttpMethod.class)))
			.forEach(method -> buildRestOperation(clazz, method, instanceProvider, syncTaskSubmitter,operations,factory));
	}
	
	private static void buildRestOperation(Class<?> clazz, Method method, InstanceProvider instanceProvider, SyncTaskSubmitter syncTaskSubmitter, ApiOperations operations, ApiContextFactory factory) {
		Object instance = instanceProvider.get(clazz);
		if(instance==null) {
			throw new IllegalArgumentException("No instance of "+clazz+" was provided");
		}else {
			build(clazz, method, instanceProvider.get(clazz),syncTaskSubmitter, operations, factory);
		}
	}

	private static void build(Class<?> clazz, Method method, Object object, SyncTaskSubmitter syncTaskSubmitter, ApiOperations operations, ApiContextFactory factory) {
		String path = path(clazz,method);
		io.netty.handler.codec.http.HttpMethod httpMethod = method(method);
		
		Map<Integer, RestParam> argumentIndexToRestParam = new HashMap<>();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Type[] parametersTypes = method.getGenericParameterTypes();

		TypeLiteral<?> bodyType = VOID_TYPE;
		Integer contexArgIndex = null;
		for (int argIndex = 0; argIndex < parameterAnnotations.length; argIndex++) {
			ResolvedType resolvedType = new TypeResolver().resolve(parametersTypes[argIndex]);
			if (resolvedType.isInstanceOf(ApiContext.class)) {
				contexArgIndex = argIndex;
			} else if (processPathParam(parameterAnnotations[argIndex],resolvedType,argumentIndexToRestParam,argIndex)) {
				continue;
			} else if (processQueryParam(parameterAnnotations[argIndex],resolvedType,argumentIndexToRestParam,argIndex)) {
				continue;
			} else if (processHeaderParam(parameterAnnotations[argIndex],resolvedType,argumentIndexToRestParam,argIndex)) {
				continue;
			} else{
				bodyType = TypeLiteral.create(parametersTypes[argIndex]);
				argumentIndexToRestParam.put(argIndex,new RestBodyParam(new TypeResolver().resolve(parametersTypes[argIndex])));
			}
		}
		
		ApiPattern apiPattern = ApiPattern.build(path);
		JaxRsMethodInvocation<?> consumer;
		if(contexArgIndex==null) {
			consumer = new JaxRsBlockingMethodInvocation<>(object,method,Collections.unmodifiableMap(argumentIndexToRestParam),syncTaskSubmitter); 
		} else {
			consumer = new JaxRsAsyncMethodInvocation<>(object,method,Collections.unmodifiableMap(argumentIndexToRestParam),contexArgIndex); 
		}
		
		
		operations.add(new MethodInvocationOperation<>(httpMethod, apiPattern, bodyType, consumer, factory));
	}
	
	private static boolean processQueryParam( Annotation[] parameterAnnotations, ResolvedType type, Map<Integer, RestParam> argumentIndexToRestParam,int argIndex) {
		Optional<QueryParam> queryParam = Stream.of(parameterAnnotations)
				.filter(ann -> ann.annotationType().equals(QueryParam.class)).findFirst()
				.map(ann -> QueryParam.class.cast(ann));
		Optional<Object> defaultValue = Stream.of(parameterAnnotations)
				.filter(ann -> ann.annotationType().equals(DefaultValue.class)).findFirst()
				.map(ann -> DefaultValue.class.cast(ann))
				.map(def->RestParam.convert(def.value(), type));
		
		if (queryParam.isPresent()) {
			argumentIndexToRestParam.put(argIndex, new RestQueryParam(type, queryParam.get().value(),
					defaultValue.orElse(null)));
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean processPathParam(Annotation[] parameterAnnotations,ResolvedType type, Map<Integer, RestParam> argumentIndexToRestParam,int argIndex) {
		Optional<PathParam> pathParam = Stream.of(parameterAnnotations)
				.filter(ann -> ann.annotationType().equals(PathParam.class)).findFirst()
				.map(ann -> PathParam.class.cast(ann));
		if (pathParam.isPresent()) {
			argumentIndexToRestParam.put(argIndex,
					new RestPathParam(type, pathParam.get().value()));
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean processHeaderParam(Annotation[] parameterAnnotations,ResolvedType type, Map<Integer, RestParam> argumentIndexToRestParam,int argIndex) {
		Optional<HeaderParam> headerParam = Stream.of(parameterAnnotations)
				.filter(ann -> ann.annotationType().equals(HeaderParam.class)).findFirst()
				.map(ann -> HeaderParam.class.cast(ann));
		Optional<Object> defaultValue = Stream.of(parameterAnnotations)
				.filter(ann -> ann.annotationType().equals(DefaultValue.class)).findFirst()
				.map(ann -> DefaultValue.class.cast(ann))
				.map(def->RestParam.convert(def.value(), type));
		
		if (headerParam.isPresent()) {
			argumentIndexToRestParam.put(argIndex,
					new RestHeaderParam(type, headerParam.get().value(),defaultValue.orElse(null)));
			return true;
		} else {
			return false;
		}
	}
	
	public static io.netty.handler.codec.http.HttpMethod method(Method method) {
		return Stream.of(method.getAnnotations())
			.filter(ann -> ann.annotationType().isAnnotationPresent(javax.ws.rs.HttpMethod.class))
			.map(ann -> ann.annotationType().getAnnotation(javax.ws.rs.HttpMethod.class).value())
			.map(io.netty.handler.codec.http.HttpMethod::valueOf).findFirst().orElse(null);
	}

	
	public static String path(Class<?> clazz, Method method) {
		StringBuilder path = new StringBuilder();
		boolean hasBasePath=false;
		if (clazz.isAnnotationPresent(Path.class)) {
			path.append(trim(clazz.getAnnotation(Path.class).value()));
			hasBasePath=true;
		}
		if(method.isAnnotationPresent(Path.class)) {
			if(hasBasePath){
				path.append('/');
			}
			path.append(trim(method.getAnnotation(Path.class).value()));
		}
		return path.toString();
	}
	
	private static String trim(String value) {
		return value.replaceAll("^/+", StringUtil.EMPTY_STRING)
				.replaceAll("/+$",  StringUtil.EMPTY_STRING);
	}


}
