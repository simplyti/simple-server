package com.simplyti.service.api.builder;

public interface BodyLengthConfigurable<T extends BodyLengthConfigurable<T>> {
	
	public T withMaximunBodyLength(int length);
	

}
