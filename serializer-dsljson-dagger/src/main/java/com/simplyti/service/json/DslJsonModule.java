package com.simplyti.service.json;

import java.util.Set;

import javax.inject.Singleton;

import com.dslplatform.json.Configuration;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.serializer.json.DslJsonSerializer;

import dagger.Module;
import dagger.Provides;

@Module(includes=Multibindings.class)
public class DslJsonModule {
	
	@Provides
	@Singleton
	public Json json(Set<Configuration> configurations) {
		return new DslJsonSerializer(configurations);
	}

}
