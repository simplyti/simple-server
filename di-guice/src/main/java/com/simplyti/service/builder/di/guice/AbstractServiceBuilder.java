package com.simplyti.service.builder.di.guice;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.google.common.base.MoreObjects;
import com.google.inject.Module;
import com.simplyti.service.Listener;
import com.simplyti.service.ServerConfig;
import com.simplyti.service.Service;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.fileserver.DirectoryResolver;
import com.simplyti.service.fileserver.FileServeConfiguration;
import com.simplyti.service.ssl.SslConfig;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

public abstract class AbstractServiceBuilder<T extends Service<?>> implements ServiceBuilder<T>{

	private static final int DEFAULT_BLOCKING_THREAD_POOL = 500;
	private static final int DEFAULT_INSECURE_PORT = 8080;
	private static final int DEFAULT_SECURE_PORT = 8443;
	
	private final Class<T> serviceClass;
	
	private String usingLogger;
	private Collection<Class<? extends ApiProvider>> apiClasses;
	private Collection<ApiProvider> apiProviders;
	private Collection<Module> modules;
	private Integer blockingThreadPool;
	private Set<Listener> listeners;
	private Integer insecuredPort;
	private Integer securedPort;
	private String name;
	
	private SslProvider sslProvider;

	private FileServeConfiguration fileServerConfig;
	private EventLoopGroup eventLoopGroup;
	
	private boolean verbose;
	
	public AbstractServiceBuilder(Class<T> serviceClass) {
		this.serviceClass=serviceClass;
	}

	@Override
	public T build() {
		ServerConfig config = new ServerConfig(
				name,
				MoreObjects.firstNonNull(blockingThreadPool, DEFAULT_BLOCKING_THREAD_POOL),
				listeners(),eventLoopGroup!=null,
				verbose);
		
		Stream<Module> additinalModules = Optional.ofNullable(modules)
				.map(Collection::stream)
				.orElse(Stream.<Module>empty());
		
		return build0(config, new SslConfig(sslProvider), fileServerConfig, serviceClass,additinalModules,
				MoreObjects.firstNonNull(apiClasses, Collections.emptySet()),
				MoreObjects.firstNonNull(apiProviders, Collections.emptySet()),
				eventLoopGroup);
	}
	
	private Set<Listener> listeners() {
		if(listeners!=null) {
			return Collections.unmodifiableSet(listeners);
		}
		
		Set<Listener> listeners = new HashSet<>();
		if(insecuredPort == null ) {
			listeners.add(new Listener(DEFAULT_INSECURE_PORT,false));
		} else if(insecuredPort>0) {
			listeners.add(new Listener(insecuredPort,false));
		}
		if(securedPort == null ) {
			listeners.add(new Listener(DEFAULT_SECURE_PORT,true));
		} else if(securedPort>0) {
			listeners.add(new Listener(securedPort,true));
		}
		return Collections.unmodifiableSet(listeners);
	}

	protected abstract T build0(ServerConfig config, SslConfig sslConfig, FileServeConfiguration fileServerConfig, Class<T> serviceClass, Stream<Module> additinalModules, Collection<Class<? extends ApiProvider>> apiClasses, Collection<ApiProvider> apiProviders,  EventLoopGroup eventLoopGroup);

	@Override
	public ServiceBuilder<T> withBlockingThreadPoolSize(int blockingThreadPool) {
		this.blockingThreadPool=blockingThreadPool;
		return this;
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
	public ServiceBuilder<T> withListener(int port, boolean ssl) {
		if(listeners == null) {
			this.listeners = new HashSet<>();
		}
		this.listeners.add(new Listener(port,ssl));
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
			this.apiClasses=new HashSet<>();
		}
		this.apiClasses.add(apiClass);
		return this;
	}
	
	@Override
	public ServiceBuilder<T> withApi(ApiProvider provider) {
		if(this.apiProviders==null){
			this.apiProviders=new HashSet<>();
		}
		this.apiProviders.add(provider);
		return this;
	}

	@Override
	public ServiceBuilder<T> withModule(Module module) {
		if(this.modules==null) {
			this.modules=new HashSet<>();
		}
		this.modules.add(module);
		return this;
	}
	
	@Override
	public ServiceBuilder<T> withName(String name) {
		this.name=name;
		return this;
	}
	
	@Override
	public ServiceBuilder<T> withSslProvider(SslProvider sslProvider) {
		this.sslProvider=sslProvider;
		return this;
	}
	
	@Override
	public ServiceBuilder<T> withModule(Class<? extends Module> module) {
		try {
			return withModule(module.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public ServiceBuilder<T> fileServe(String path, String directory) {
		this.fileServerConfig=new FileServeConfiguration(Pattern.compile("^"+path+"/(.*)$"),DirectoryResolver.literal(directory));
		return this;
	}

	@Override
	public ServiceBuilder<T> eventLoopGroup(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup=eventLoopGroup;
		return this;
	}

	@Override
	public ServiceBuilder<T> verbose() {
		this.verbose=true;
		return this;
	}

}
