package com.simplyti.server.http.api.builder;

public interface HttpMethodApiBuilder {

	ResponseTypableApiBuilder get(String path);
	
	ResponseTypableApiBuilder delete(String path);

	RequestTypableApiBuilder post(String path);
	
	RequestTypableApiBuilder put(String path);
	
	RequestTypableApiBuilder patch(String path);

}
