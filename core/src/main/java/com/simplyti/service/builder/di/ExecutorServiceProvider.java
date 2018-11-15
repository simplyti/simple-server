package com.simplyti.service.builder.di;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;

import io.netty.util.concurrent.DefaultThreadFactory;

public class ExecutorServiceProvider implements Provider<ExecutorService> {

	@Override
	public ExecutorService get() {
		return new ThreadPoolExecutor(1, 100,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new DefaultThreadFactory("blockingGroup"));
	}

}

