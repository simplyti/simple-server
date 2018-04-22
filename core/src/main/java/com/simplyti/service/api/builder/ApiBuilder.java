package com.simplyti.service.api.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;

import com.google.inject.Injector;
import com.simplyti.service.api.ApiOperation;
import com.simplyti.service.api.builder.jaxrs.JaxRSBuilder;

public class ApiBuilder {

	private final List<ApiOperation<?,?>> operations;
	private final Injector injector;
	private final ExecutorService blockingExecutor;
	
	@Inject
	public ApiBuilder(Injector injector,ExecutorService blockingExecutor){
		operations = new ArrayList<>();
		this.injector=injector;
		this.blockingExecutor=blockingExecutor;
	}

	public MethodApiBuilder when() {
		return new MethodApiBuilder(this);
	}

	public void add(ApiOperation<?,?> operation) {
		this.operations.add(operation);
	}

	public List<ApiOperation<?,?>> get() {
		return operations;
	}

	public void usingJaxRSContract(Class<?> clazz) {
		Stream.of(clazz.getDeclaredMethods())
		.filter(method -> Stream.of(method.getAnnotations()).anyMatch(ann -> ann.annotationType().isAnnotationPresent(HttpMethod.class)))
		.forEach(method -> buildRestOperation(clazz, method));
	}

	private void buildRestOperation(Class<?> clazz, Method method) {
		JaxRSBuilder.build(this, clazz, method, injector.getInstance(clazz),blockingExecutor);
	}

}