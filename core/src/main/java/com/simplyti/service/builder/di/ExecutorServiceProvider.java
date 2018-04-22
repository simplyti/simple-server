package com.simplyti.service.builder.di;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;

import io.netty.util.concurrent.DefaultThreadFactory;

public class ExecutorServiceProvider implements Provider<ExecutorService> {

	@Override
	public ExecutorService get() {
		return new ThreadPoolExecutor(0, 50,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new DefaultThreadFactory("blockingGroup"));
	}

}

