package com.simplyti.service.api.filter;

public interface FilterContext<T> {

	public void done();

	public void fail(Throwable unauthorizedException);

	public T object();

}
