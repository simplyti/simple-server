package com.simplyti.service.aws.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import javax.inject.Inject;

import com.amazonaws.services.lambda.runtime.Context;
import com.simplyti.service.ServerStopAdvisor;
import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;
import com.simplyti.service.transport.ServerTransport;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Promise;

public class AWSLambdaService implements ServerTransport {
	
	private final LambdaChannelPool embededChannelPool;
	private final EventLoopGroup eventLoopGroup;
	
	@Inject
	public AWSLambdaService(EventLoopGroup eventLoopGroup, ServerStopAdvisor startStopMonitor,
			@StartStopLoop EventLoop startStopLoop, ClientChannelGroup clientChannelGroup,
			Set<ServerStartHook> serverStartHook,Set<ServerStopHook> serverStopHook,
			LambdaChannelPool embededChannelPool, ServerConfig config){
		this.eventLoopGroup=eventLoopGroup;
		this.embededChannelPool=embededChannelPool;
	}

	public Future<Void> handle(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		EventLoop eventLoop = eventLoopGroup.next();
		Promise<Void> promise = eventLoop.newPromise();
		LambdaChannel channel = embededChannelPool.get(outputStream,eventLoop,promise);
		channel.read(inputStream);
		return new DefaultFuture<>(promise,eventLoop);
	}

	@Override
	public Future<Void> start(EventLoop eventLoop) {
		return new DefaultFuture<>( eventLoop.newSucceededFuture(null),eventLoop);
	}

	@Override
	public Future<Void> stop(EventLoop eventLoop) {
		return new DefaultFuture<>(eventLoop.newSucceededFuture(null),eventLoop);
	}


}
