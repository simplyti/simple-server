package com.simplyti.service.aws.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import javax.inject.Inject;

import com.amazonaws.services.lambda.runtime.Context;
import com.simplyti.service.AbstractService;
import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class AWSLambdaService extends AbstractService<AWSLambdaService>{
	
	private final LambdaChannelPool embededChannelPool;
	
	@Inject
	public AWSLambdaService(EventLoopGroup eventLoopGroup,
			@StartStopLoop EventLoop startStopLoop, ClientChannelGroup clientChannelGroup,
			Set<ServerStartHook> serverStartHook,Set<ServerStopHook> serverStopHook,
			LambdaChannelPool embededChannelPool){
		super(eventLoopGroup,startStopLoop,clientChannelGroup,serverStartHook,serverStopHook);
		this.embededChannelPool=embededChannelPool;
	}

	@Override
	protected Future<Void> bind(EventLoop executor) {
		return executor.newSucceededFuture(null) ;
	}

	@Override
	protected Future<Void> undbind(EventLoop executor) {
		return executor.newSucceededFuture(null) ;
	}

	public Future<Void> handle(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		EventLoop eventLoop = eventLoopGroup().next();
		Promise<Void> promise = eventLoop.newPromise();
		LambdaChannel channel = embededChannelPool.get(outputStream,eventLoop,promise);
		channel.read(inputStream);
		return promise;
	}


}
