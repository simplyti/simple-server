package com.simplyti.service.toremove;

import java.time.LocalDateTime;

import com.dslplatform.json.Configuration;
import com.dslplatform.json.DslJson;

public class LocalDateTimeConfiguration implements Configuration {

	@SuppressWarnings("unchecked")
	@Override
	public void configure(@SuppressWarnings("rawtypes") DslJson json) {
		json.registerWriter(LocalDateTime.class, new LocalDateTimeWriter());
	}

}
