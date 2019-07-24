package com.simplyti.service.serializer.json;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.simplyti.service.api.serializer.json.Json;

public class JsoniterModule extends AbstractModule {
	
	protected void configure() {
		bind(Json.class).to(Jsoniter.class).in(Singleton.class);
	}

}
