package com.simplyti.service.builder.di.guice;

import javax.inject.Inject;

import com.google.inject.Injector;
import com.simplyti.service.api.builder.di.InstanceProvider;

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

}
