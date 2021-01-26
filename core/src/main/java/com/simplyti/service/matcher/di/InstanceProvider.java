package com.simplyti.service.matcher.di;

import java.lang.annotation.Annotation;

public interface InstanceProvider {

	public <T> T get(Class<T> clazz);
	
	public <T> T get(Class<T> clazz, Class<? extends Annotation> ann);

}
