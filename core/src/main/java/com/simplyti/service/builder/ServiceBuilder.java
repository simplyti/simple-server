package com.simplyti.service.builder;

import com.google.inject.Module;
import com.simplyti.service.Service;
import com.simplyti.service.api.builder.ApiProvider;

public interface ServiceBuilder {

	public Service build();

	public ServiceBuilder withLog4J2Logger();

	public ServiceBuilder withApi(Class<? extends ApiProvider> apiClass);

	public ServiceBuilder insecuredPort(int port);
	
	public ServiceBuilder securedPort(int port);

	public ServiceBuilder withModule(Class<? extends Module> module);
	
	public ServiceBuilder withModule(Module kube2ovnModule);

	public ServiceBuilder fileServe(String path, String directory);

}
