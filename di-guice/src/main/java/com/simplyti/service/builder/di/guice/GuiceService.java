package com.simplyti.service.builder.di.guice;

import com.simplyti.service.DefaultService;
import com.simplyti.service.Service;

public interface GuiceService {
	
	public static GuiceServiceBuilder<DefaultService> builder() {
		return builder(DefaultService.class);
	}
	
	public static <T extends Service<T>> GuiceServiceBuilder<T> builder(Class<T> serviceClass) {
		return new GuiceServiceBuilder<>(serviceClass);
	}
	
}
