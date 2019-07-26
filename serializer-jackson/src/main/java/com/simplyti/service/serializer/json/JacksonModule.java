package com.simplyti.service.serializer.json;

import com.fasterxml.jackson.databind.Module;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.api.serializer.json.Json;

public class JacksonModule extends AbstractModule {

	@Override
	public void configure() {
		bind(Json.class).to(JacksonJson.class).in(Singleton.class);
		Multibinder.newSetBinder(binder(), Module.class);
	}

}
