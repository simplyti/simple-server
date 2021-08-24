package com.simplyti.service.filter;

public interface Filter<T> {
	
	void execute(FilterContext<T> context);

}
