package com.simplyti.service.injector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.simplyti.service.Service;
import com.simplyti.service.client.DefaultSimpleHttpClient;
import com.simplyti.service.client.SimpleHttpClient;
import com.simplyti.service.clients.http.HttpClient;

import cucumber.runtime.java.guice.InjectorSource;
import cucumber.runtime.java.guice.ScenarioScoped;
import cucumber.runtime.java.guice.impl.ScenarioModule;
import cucumber.runtime.java.guice.impl.SequentialScenarioScope;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;

public class CustomInjectorSource extends AbstractModule implements InjectorSource{

	@Override
	public Injector getInjector() {
		 ScenarioModule scenarioModule = new ScenarioModule(new SequentialScenarioScope());
         return Guice.createInjector(scenarioModule,this);
	}

	@Override
	protected void configure() {
		bind(HttpClient.class).annotatedWith(Names.named("scenario")).toProvider(HttpClientProvider.class).in(ScenarioScoped.class);
		bind(HttpClient.class).annotatedWith(Names.named("singleton")).toProvider(HttpClientProvider.class).in(Singleton.class);
		
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
		bind(EventLoopGroup.class).toInstance(eventLoopGroup);
		SimpleHttpClient client = new DefaultSimpleHttpClient(eventLoopGroup);
		bind(SimpleHttpClient.class).toInstance(client);
		
		bind(new TypeLiteral<Map<String,Object>>(){}).toProvider(Maps::newHashMap).in(ScenarioScoped.class);
		bind(new TypeLiteral<List<Future<Service>>>(){}).toProvider(ArrayList::new).in(ScenarioScoped.class);
	}
	

}
