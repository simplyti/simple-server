package com.simplyti.service.steps;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.serializer.json.Jsoniter;

public class JsoniterModule extends AbstractModule {
	
	protected void configure() {
		bind(Json.class).to(Jsoniter.class).in(Singleton.class);
	}

}

