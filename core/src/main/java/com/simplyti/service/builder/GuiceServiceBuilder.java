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

import io.netty.channel.EventLoopGroup;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import io.vavr.control.Try;

public class GuiceServiceBuilder<T extends Service<?>> implements ServiceBuilder<T> {

	private static final int DEFAULT_INSECURE_PORT = 8080;
	private static final int DEFAULT_SECURE_PORT = 8443;
	
	private final Class<T> serviceClass;
	
	private String usingLogger;
	private Collection<Class<? extends ApiProvider>> apiClasses;
	private Collection<Module> modules;
	private Integer insecuredPort;
	private Integer securedPort;

	private FileServeConfiguration fileServe;
	private EventLoopGroup eventLoopGroup;

	public GuiceServiceBuilder(Class<T> serviceClass) {
		this.serviceClass=serviceClass;
	}

	@Override
	public T build() {
		ServerConfig config = new ServerConfig(
				serviceClass,
				MoreObjects.firstNonNull(insecuredPort, DEFAULT_INSECURE_PORT),
				MoreObjects.firstNonNull(securedPort, DEFAULT_SECURE_PORT),fileServe,eventLoopGroup!=null);
		ServiceModule coreModule = new ServiceModule(config,MoreObjects.firstNonNull(apiClasses, Collections.emptySet()),eventLoopGroup);
		Stream<Module> additinalModules = Optional.ofNullable(modules)
				.map(Collection::stream)
				.orElse(Stream.<Module>empty());
		Injector injector = Guice.createInjector(Stream.concat(additinalModules, Stream.of(coreModule)).collect(Collectors.toList()));
		return injector.getInstance(serviceClass);
	}
	
	@Override
	public ServiceBuilder<T> insecuredPort(int port) {
		this.insecuredPort=port;
		return this;
	}
	
	@Override
	public ServiceBuilder<T> disableInsecurePort() {
		this.insecuredPort=-1;
		return this;
	}
	
	@Override
	public ServiceBuilder<T> disableSecuredPort() {
		this.securedPort=-1;
		return this;
	}
	
	@Override
	public ServiceBuilder<T> securedPort(int port) {
		this.securedPort=port;
		return this;
	}

	@Override
	public ServiceBuilder<T> withLog4J2Logger() {
		checkState(usingLogger==null,String.format("Logger already stablished to %s", usingLogger));
		InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
		this.usingLogger="log4j2";
		return this;
	}
	
	@Override
	public ServiceBuilder<T> withSlf4jLogger() {
		checkState(usingLogger==null,String.format("Logger already stablished to %s", usingLogger));
		InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
		this.usingLogger="slf4j";
		return this;
	}

	@Override
	public ServiceBuilder<T> withApi(Class<? extends ApiProvider> apiClass) {
		if(this.apiClasses==null){
			this.apiClasses=Sets.newHashSet();
		}
		this.apiClasses.add(apiClass);
		return this;
	}

	@Override
	public ServiceBuilder<T> withModule(Module module) {
		if(this.modules==null) {
			this.modules=Sets.newHashSet();
		}
		this.modules.add(module);
		return this;
	}
	
	@Override
	public ServiceBuilder<T> withModule(Class<? extends Module> module) {
		return withModule(Try.of(module::newInstance).get());
	}

	@Override
	public ServiceBuilder<T> fileServe(String path, String directory) {
		this.fileServe=new FileServeConfiguration(Pattern.compile("^"+path+"/(.*)$"),DirectoryResolver.literal(directory));
		return this;
	}

	@Override
	public ServiceBuilder<T> eventLoopGroup(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup=eventLoopGroup;
		return this;
	}

}
