package com.simplyti.service.builder.di.guice.apibuilder;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.simplyti.service.matcher.di.InstanceProvider;

public class GuiceInstanceProvider implements InstanceProvider {
	
	private final Injector injector;

	@Inject
	public GuiceInstanceProvider(Injector injector) {
		this.injector=injector;
	}

	@Override
	public <T> T get(Class<T> clazz) {
		return injector.getInstance(clazz);
	}

	@Override
	public <T> T get(Class<T> clazz, Class<? extends Annotation> ann) {
		return injector.getInstance(Key.get(clazz, ann));
	}

}
