package com.simplyti.service.api.builder.di;

public interface InstanceProvider {

	public <T> T get(Class<T> clazz);

}
