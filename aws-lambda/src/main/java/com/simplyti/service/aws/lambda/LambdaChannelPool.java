package com.simplyti.service.aws.lambda;

import java.io.OutputStream;
import java.util.Queue;

import javax.inject.Inject;

import com.google.common.collect.Queues;
import com.simplyti.service.aws.lambda.handler.OutputStreamHandler;
import com.simplyti.service.channel.ServiceChannelInitializer;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Promise;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor=@__(@Inject))
public class LambdaChannelPool {
	
	private final Queue<LambdaChannel> channels = Queues.newConcurrentLinkedQueue();
	private final ServiceChannelInitializer channelInitHandler;

	public LambdaChannel get(OutputStream outputStream,Promise<Void> promise) {
		LambdaChannel channel = channels.poll();
		if(channel==null) {
			channel = new LambdaChannel(channelInitHandler);
		}
		channel.pipeline().addFirst(new OutputStreamHandler(this,outputStream,promise));
		return channel;
	}

	public void offer(Channel channel) {
		channel.pipeline().removeFirst();
		channels.offer((LambdaChannel) channel);
	}

}
