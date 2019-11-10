package com.simplyti.service;

import com.dslplatform.json.Configuration;
import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.JsonWriter.WriteObject;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.examples.api.SerializedErrorDTO;

public class JsonWroteErrorModule extends AbstractModule implements Configuration , WriteObject<SerializedErrorDTO> {
	
	protected void configure() {
		Multibinder.newSetBinder(binder(), Configuration.class).addBinding().toInstance(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void configure(@SuppressWarnings("rawtypes") DslJson json) {
		json.registerWriter(SerializedErrorDTO.class, this);
	}

	@Override
	public void write(JsonWriter writer, SerializedErrorDTO value) {
		throw new RuntimeException("Error coding json");
	}

}
