package com.simplyti.service.builder.di.dagger.apibuilder;

import java.util.Map;

import com.simplyti.service.api.builder.di.InstanceProvider;

public class DaggerInstanceProvider implements InstanceProvider {

	private Map<Class<?>, Object> instances;

	public DaggerInstanceProvider(Map<Class<?>, Object> instances) {
		this.instances=instances;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Class<T> clazz) {
		return (T) instances.get(clazz);
	}

}
