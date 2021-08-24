package com.simplyti.service.builder.di.guice;


public interface GuiceService {
	
	public static GuiceServiceBuilder builder() {
		return new GuiceServiceBuilder();
	}
	
}
