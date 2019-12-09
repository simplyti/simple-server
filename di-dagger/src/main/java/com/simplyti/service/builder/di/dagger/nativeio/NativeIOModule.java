package com.simplyti.service.builder.di.dagger.nativeio;

import javax.inject.Singleton;

import com.simplyti.service.builder.di.NativeIO;

import dagger.Module;
import dagger.Provides;

@Module
public class NativeIOModule {
	
	@Provides
	@Singleton
	public NativeIO nativeIO() {
		return new NativeIO();
	}

}
