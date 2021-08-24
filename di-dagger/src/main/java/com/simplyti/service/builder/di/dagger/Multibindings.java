package com.simplyti.service.builder.di.dagger;

import java.util.Set;

import com.simplyti.server.http.api.filter.OperationInboundFilter;
import com.simplyti.service.channel.handler.inits.ServiceHadlerInit;
import com.simplyti.service.filter.http.FullHttpRequestFilter;
import com.simplyti.service.filter.http.HttpRequestFilter;
import com.simplyti.service.filter.http.HttpResponseFilter;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;
import com.simplyti.service.ssl.SslHandlerFactory;

import dagger.BindsOptionalOf;
import dagger.Module;
import dagger.multibindings.Multibinds;

@Module
public abstract class Multibindings {
	
	@Multibinds abstract Set<OperationInboundFilter> operationInboundFilters();
	
	@Multibinds abstract Set<ServiceHadlerInit> serviceHandlerInits();
	
	@Multibinds abstract Set<HttpRequestFilter> httpRequestFilters();
	@Multibinds abstract Set<FullHttpRequestFilter> fullHttpRequestFilters();
	@Multibinds abstract Set<HttpResponseFilter> httpResponseFilters();
	
	@Multibinds abstract Set<ServerStartHook> serverStartHooks();
	@Multibinds abstract Set<ServerStopHook> serverStopHooks();
	
	@BindsOptionalOf abstract SslHandlerFactory sslHandlerFactory();
	
}
