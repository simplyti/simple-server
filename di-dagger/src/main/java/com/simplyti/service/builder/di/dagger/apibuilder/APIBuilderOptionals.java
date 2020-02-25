package com.simplyti.service.builder.di.dagger.apibuilder;

import java.util.Map;
import java.util.Set;

import com.simplyti.server.http.api.ApiProvider;

import dagger.Module;
import dagger.multibindings.Multibinds;

@Module
public abstract class APIBuilderOptionals {

	@Multibinds abstract Set<ApiProvider> providers();
	@Multibinds abstract Map<Class<?>, Object> instances();
	
}
