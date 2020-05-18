package com.simplyti.service.builder.di.dagger;

import java.util.Set;

import com.simplyti.service.Listener;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.api.filter.HttpResponseFilter;
import com.simplyti.service.api.filter.OperationInboundFilter;
import com.simplyti.service.builder.di.NativeIO;
import com.simplyti.service.channel.handler.inits.HandlerInit;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;
import com.simplyti.service.ssl.SslHandlerFactory;

import dagger.BindsOptionalOf;
import dagger.Module;
import dagger.multibindings.Multibinds;

@Module
public abstract class Multibindings {
	
	@Multibinds abstract Set<OperationInboundFilter> operationInboundFilters();
	
	@Multibinds abstract Set<HandlerInit> handlerInits();
	
	@Multibinds abstract Set<HttpRequestFilter> httpRequestFilters();
	@Multibinds abstract Set<HttpResponseFilter> httpResponseFilters();
	
	@Multibinds abstract Set<ServerStartHook> serverStartHooks();
	@Multibinds abstract Set<ServerStopHook> serverStopHooks();
	
	@Multibinds abstract Set<Listener> listeners();
	
	@BindsOptionalOf abstract SslHandlerFactory sslHandlerFactory();
	@BindsOptionalOf abstract NativeIO nativeIO();
	
}
