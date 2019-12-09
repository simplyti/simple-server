package com.simplyti.service.builder.di.guice.nativeio;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.simplyti.service.builder.di.NativeIO;

public class NativeIOModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(NativeIO.class).in(Singleton.class);
	}

}
