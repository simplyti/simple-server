package com.simplyti.service.builder.di;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.ServerConfig;

import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor=@__(@Inject))
public class ExecutorServiceProvider implements Provider<ExecutorService> {
	
	private final ServerConfig config;

	@Override
	public ExecutorService get() {
		return new ThreadPoolExecutor(0, config.blockingThreadPool(),
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new DefaultThreadFactory("blockingGroup"));
	}

}

