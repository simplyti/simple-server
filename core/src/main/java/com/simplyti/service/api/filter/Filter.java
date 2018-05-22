package com.simplyti.service.api.filter;

public interface Filter<T> {
	
	void execute(FilterContext<T> context);

}
