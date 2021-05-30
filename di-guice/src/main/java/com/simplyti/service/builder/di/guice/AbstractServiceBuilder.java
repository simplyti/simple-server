package com.simplyti.service.builder.di.guice;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.google.inject.Module;
import com.simplyti.service.DefaultServer;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.fileserver.DirectoryResolver;
import com.simplyti.service.fileserver.FileServeConfiguration;
import com.simplyti.service.ssl.SslConfig;
import com.simplyti.service.transport.Listener;
import com.simplyti.service.transport.tcp.TcpListener;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

public abstract class AbstractServiceBuilder implements ServiceBuilder, Listenable {

	private static final int DEFAULT_BLOCKING_THREAD_POOL = 500;
	private static final Collection<Listener> DEFAULT_LISTENERS = Arrays.asList(new TcpListener(8080,false),new TcpListener(8443,true));
	private static final int DEFAULT_MAX_BODY_SIZE = 10000000;
	
	private Collection<Class<? extends ApiProvider>> apiClasses;
	private Collection<Module> modules;
	private Collection<Listener> listeners;
	private Integer blockingThreadPool;
	private String name;
	private SslProvider sslProvider;
	private FileServeConfiguration fileServerConfig;
	private EventLoopGroup eventLoopGroup;
	private boolean verbose;
	private Integer maxBodySize;
	
	@Override
	public DefaultServer build() {
		ServerConfig config = new ServerConfig(
				name,
				firstNonNull(blockingThreadPool, DEFAULT_BLOCKING_THREAD_POOL),
				firstNonNull(listeners,DEFAULT_LISTENERS),
				eventLoopGroup!=null,
				verbose,
				firstNonNull(maxBodySize, DEFAULT_MAX_BODY_SIZE));
		
		Stream<Module> additinalModules = Optional.ofNullable(modules)
				.map(Collection::stream)
				.orElse(Stream.<Module>empty());
		
		return build0(config, new SslConfig(sslProvider), fileServerConfig,additinalModules,
				firstNonNull(apiClasses, Collections.emptySet()),
				eventLoopGroup);
	}
	
	private <Q> Q firstNonNull(Q obj1, Q obj2) {
		if(obj1 !=null) {
			return obj1;
		} 
		return obj2;
	}

	protected abstract DefaultServer build0(ServerConfig config, SslConfig sslConfig, FileServeConfiguration fileServerConfig, Stream<Module> additinalModules, Collection<Class<? extends ApiProvider>> apiClasses, EventLoopGroup eventLoopGroup);

	@Override
	public ServiceBuilder withBlockingThreadPoolSize(int blockingThreadPool) {
		this.blockingThreadPool=blockingThreadPool;
		return this;
	}
	
	@Override
	public ServiceBuilder withLog4J2Logger() {
		InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
		return this;
	}
	
	@Override
	public ServiceBuilder withSlf4jLogger() {
		InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
		return this;
	}

	@Override
	public ServiceBuilder withApi(Class<? extends ApiProvider> apiClass) {
		if(this.apiClasses==null){
			this.apiClasses=new HashSet<>();
		}
		this.apiClasses.add(apiClass);
		return this;
	}
	
	@Override
	public ServiceBuilder withModule(Module module) {
		if(this.modules==null) {
			this.modules=new HashSet<>();
		}
		this.modules.add(module);
		return this;
	}
	
	@Override
	public ServiceBuilder withName(String name) {
		this.name=name;
		return this;
	}
	
	@Override
	public ServiceBuilder withSslProvider(SslProvider sslProvider) {
		this.sslProvider=sslProvider;
		return this;
	}
	
	@Override
	public ServiceBuilder withModule(Class<? extends Module> module) {
		try {
			return withModule(module.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public ServiceBuilder withFileServe(String path, String directory) {
		this.fileServerConfig=new FileServeConfiguration(Pattern.compile("^"+path+"/(.*)$"),DirectoryResolver.literal(directory));
		return this;
	}

	@Override
	public ServiceBuilder withEventLoopGroup(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup=eventLoopGroup;
		return this;
	}

	@Override
	public ServiceBuilder verbose() {
		this.verbose=true;
		return this;
	}
	
	@Override
	public ServiceBuilder withMaxBodySize(int maxBodySize) {
		this.maxBodySize=maxBodySize;
		return this;
	}
	
	@Override
	public ListenerBuilder withListener() {
		return new DefaultListenerBuilder(this);
	}
	
	@Override
	public ServiceBuilder listen(Listener listener) {
		if(this.listeners==null) {
			this.listeners=new HashSet<>();
		}
		this.listeners.add(listener);
		return this;
	}

}
