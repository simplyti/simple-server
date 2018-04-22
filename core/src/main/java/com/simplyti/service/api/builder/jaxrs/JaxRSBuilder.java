package com.simplyti.service.api.builder.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.FinishableApiBuilder;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.internal.StringUtil;

public class JaxRSBuilder<I,O> extends FinishableApiBuilder<I, O>{

	@SuppressWarnings("unchecked")
	private static final TypeLiteral<Object> VOID_TYPE = TypeLiteral.create(Void.class);

	private JaxRSBuilder(ApiBuilder builder, HttpMethod method, String uri, TypeLiteral<I> requestType) {
		super(builder, method, uri, requestType, false);
	}

	@SuppressWarnings("unchecked")
	public static void build(ApiBuilder builder, Class<?> clazz, Method method, Object instance, ExecutorService blockingExecutor) {
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
		
		HttpMethod httpMethod = Stream.of(method.getAnnotations())
				.filter(ann -> ann.annotationType().isAnnotationPresent(javax.ws.rs.HttpMethod.class))
				.map(ann -> ann.annotationType().getAnnotation(javax.ws.rs.HttpMethod.class).value())
				.map(HttpMethod::valueOf).findFirst().get();
		
		Builder<Integer, RestParam> argumentIndexToRestParam = ImmutableMap.<Integer, RestParam>builder();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Type[] parametersTypes = method.getGenericParameterTypes();
		
		TypeLiteral<Object> bodyType = VOID_TYPE;
		Integer contexArgIndex = null;
		for (int argIndex = 0; argIndex < parameterAnnotations.length; argIndex++) {
			ResolvedType resolvedType = new TypeResolver().resolve(parametersTypes[argIndex]);
			if (resolvedType.isInstanceOf(JAXRSApiContext.class)) {
				contexArgIndex = argIndex;
			} else if (processPathParam(parameterAnnotations[argIndex],resolvedType,argumentIndexToRestParam,argIndex)) {
				continue;
			} else if (processQueryParam(parameterAnnotations[argIndex],resolvedType,argumentIndexToRestParam,argIndex)) {
				continue;
			}else{
				bodyType = TypeLiteral.create(parametersTypes[argIndex]);
				argumentIndexToRestParam.put(argIndex,new RestBodyParam(new TypeResolver().resolve(parametersTypes[argIndex])));
			}
		}
		
		new JaxRSBuilder<Object, Object>(builder, httpMethod, path.toString(), bodyType)
			.then(new MethodInvocation(argumentIndexToRestParam.build(),contexArgIndex,method,instance,blockingExecutor));
	}
	
	private static String trim(String value) {
		return value.replaceAll("^/+", StringUtil.EMPTY_STRING)
				.replaceAll("/+$",  StringUtil.EMPTY_STRING);
	}

	private static boolean processQueryParam( Annotation[] parameterAnnotations, ResolvedType type, Builder<Integer, RestParam> argumentIndexToRestParam,int argIndex) {
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

	private static boolean processPathParam(Annotation[] parameterAnnotations,ResolvedType type, Builder<Integer, RestParam> argumentIndexToRestParam,int argIndex) {
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

}
