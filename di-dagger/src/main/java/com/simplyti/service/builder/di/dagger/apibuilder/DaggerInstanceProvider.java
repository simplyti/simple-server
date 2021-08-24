package com.simplyti.service.builder.di.dagger.apibuilder;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.simplyti.service.matcher.di.InstanceProvider;

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

	@Override
	public <T> T get(Class<T> clazz, Class<? extends Annotation> ann) {
		throw new IllegalStateException();
	}

}
