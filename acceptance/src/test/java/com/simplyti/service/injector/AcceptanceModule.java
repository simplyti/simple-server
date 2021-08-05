package com.simplyti.service.injector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.simplyti.service.Server;
import com.simplyti.service.aws.lambda.AWSLambda;
import com.simplyti.service.client.DefaultSimpleHttpClient;
import com.simplyti.service.client.SimpleHttpClient;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.KubeClient;
import com.simplyti.service.proxy.ProxyServerModule;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;

public class AcceptanceModule extends AbstractModule {
	
	@Override
	protected void configure() {
        install(new ProxyServerModule());
        
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		bind(EventLoopGroup.class).toInstance(eventLoopGroup);
		
		bind(HttpClient.class).annotatedWith(Names.named("scenario")).toProvider(HttpClientProvider.class).in(ScenarioScoped.class);
		bind(HttpClient.class).annotatedWith(Names.named("singleton")).toProvider(HttpClientProvider.class).in(Singleton.class);
	
		bind(SimpleHttpClient.class).toInstance(new DefaultSimpleHttpClient(eventLoopGroup));
		
		bind(new TypeLiteral<Map<String,Object>>(){}).toProvider(Maps::newHashMap).in(ScenarioScoped.class);
		bind(new TypeLiteral<List<Future<Server>>>(){}).toProvider(ArrayList::new).in(ScenarioScoped.class);
		bind(new TypeLiteral<List<AWSLambda>>(){}).toProvider(ArrayList::new).in(ScenarioScoped.class);
		
		bind(KubeClient.class)
			.toInstance(KubeClient.builder()
				.eventLoopGroup(eventLoopGroup)
				.server("http://localhost:8082")
			.build());
		
	}

}
