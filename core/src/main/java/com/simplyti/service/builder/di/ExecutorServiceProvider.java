package com.simplyti.service.builder.di;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.config.ServerConfig;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor=@__(@Inject))
public class ExecutorServiceProvider implements Provider<ExecutorService> {
	
	private final ServerConfig config;

	@Override
	public ExecutorService get() {
		return new DefaultEventLoopGroup(config.blockingThreadPool(), new DefaultThreadFactory("blockingGroup"));
	}

}

