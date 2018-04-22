package com.simplyti.service.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.simplyti.service.Service;
import com.simplyti.service.ServerConfig;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.builder.di.ServiceModule;
import com.simplyti.service.fileserver.DirectoryResolver;
import com.simplyti.service.fileserver.FileServeConfiguration;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.vavr.control.Try;

public class GuiceServiceBuilder implements ServiceBuilder {

	private static final int DEFAULT_INSECURE_PORT = 8080;
	private static final int DEFAULT_SECURE_PORT = 8443;
	
	private String usingLogger;
	private Collection<Class<? extends ApiProvider>> apiClasses;
	private Collection<Module> modules;
	private Integer insecuredPort;
	private Integer securedPort;

	private FileServeConfiguration fileServe;

	@Override
	public Service build() {
		ServerConfig config = new ServerConfig(
				MoreObjects.firstNonNull(insecuredPort, DEFAULT_INSECURE_PORT),
				MoreObjects.firstNonNull(securedPort, DEFAULT_SECURE_PORT),fileServe);
		ServiceModule coreModule = new ServiceModule(config,MoreObjects.firstNonNull(apiClasses, Collections.emptySet()));
		Stream<Module> additinalModules = Optional.ofNullable(modules)
				.map(modules->modules.stream())
				.orElse(Stream.<Module>empty());
		Injector injector = Guice.createInjector(Stream.concat(additinalModules, Stream.of(coreModule)).collect(Collectors.toList()));
		return injector.getInstance(Service.class);
	}
	
	@Override
	public ServiceBuilder insecuredPort(int port) {
		this.insecuredPort=port;
		return this;
	}
	
	@Override
	public ServiceBuilder securedPort(int port) {
		this.securedPort=port;
		return this;
	}

	@Override
	public ServiceBuilder withLog4J2Logger() {
		checkState(usingLogger==null,String.format("Logger already stablished to %s", usingLogger));
		InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
		this.usingLogger="log4j2";
		return this;
	}

	@Override
	public ServiceBuilder withApi(Class<? extends ApiProvider> apiClass) {
		if(this.apiClasses==null){
			this.apiClasses=Sets.newHashSet();
		}
		this.apiClasses.add(apiClass);
		return this;
	}

	@Override
	public ServiceBuilder withModule(Module module) {
		if(this.modules==null) {
			this.modules=Sets.newHashSet();
		}
		this.modules.add(module);
		return this;
	}
	
	@Override
	public ServiceBuilder withModule(Class<? extends Module> module) {
		return withModule(Try.of(module::newInstance).get());
	}

	@Override
	public ServiceBuilder fileServe(String path, String directory) {
		this.fileServe=new FileServeConfiguration(Pattern.compile("^"+path+"/(.*)$"),DirectoryResolver.literal(directory));
		return this;
	}

}
