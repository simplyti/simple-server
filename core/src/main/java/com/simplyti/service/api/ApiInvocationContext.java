package com.simplyti.service.api;

import java.util.List;

public interface ApiInvocationContext<I,O> extends APIContext<O>{
	
	public I body();
	public List<String> queryParams(String name);
	public String queryParam(String name);
	public String pathParam(String key);
	
}
