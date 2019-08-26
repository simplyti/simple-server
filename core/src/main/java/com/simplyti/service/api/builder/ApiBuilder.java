package com.simplyti.service.api.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;

import com.simplyti.service.api.ApiOperation;
import com.simplyti.service.api.builder.di.InstanceProvider;
import com.simplyti.service.api.builder.jaxrs.JaxRSBuilder;
import com.simplyti.service.sync.SyncTaskSubmitter;

public class ApiBuilder {

	private final List<ApiOperation<?,?,?>> operations;
	private final InstanceProvider instanceProvider;
	private final SyncTaskSubmitter syncTaskSubmitter;
	
	@Inject
	public ApiBuilder(InstanceProvider instanceProvider,SyncTaskSubmitter syncTaskSubmitter){
		operations = new ArrayList<>();
		this.instanceProvider=instanceProvider;
		this.syncTaskSubmitter=syncTaskSubmitter;
	}

	public MethodApiBuilder when() {
		return new MethodApiBuilder(this);
	}

	public void add(ApiOperation<?,?,?> operation) {
		this.operations.add(operation);
	}

	public List<ApiOperation<?,?,?>> get() {
		return operations;
	}

	public ApiBuilder usingJaxRSContract(Class<?> clazz) {
		Stream.of(clazz.getDeclaredMethods())
		.filter(method -> Stream.of(method.getAnnotations()).anyMatch(ann -> ann.annotationType().isAnnotationPresent(HttpMethod.class)))
		.forEach(method -> buildRestOperation(clazz, method));
		return this;
	}

	private void buildRestOperation(Class<?> clazz, Method method) {
		Object instance = instanceProvider.get(clazz);
		if(instance==null) {
			throw new IllegalArgumentException("No instance of "+clazz+" was provided");
		}else {
			JaxRSBuilder.build(this, clazz, method, instanceProvider.get(clazz),syncTaskSubmitter);
		}
	}

}