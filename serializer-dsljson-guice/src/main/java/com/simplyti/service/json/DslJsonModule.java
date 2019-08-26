package com.simplyti.service.json;

import com.dslplatform.json.Configuration;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.serializer.json.DslJsonSerializer;

public class DslJsonModule extends AbstractModule {
	
	protected void configure() {
		bind(Json.class).to(DslJsonSerializer.class).in(Singleton.class);
		Multibinder.newSetBinder(binder(), Configuration.class);
	}

}