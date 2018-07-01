package com.simplyti.service.clients.http.request;

import com.simplyti.service.clients.ClientResponseFuture;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class DefaultStreamedHttpRequest implements StreamedHttpRequest {

	private final ClientResponseFuture<Void> clientFuture;
	
	public DefaultStreamedHttpRequest(ClientResponseFuture<Void> clientFuture) {
		this.clientFuture=clientFuture;
	}

	@Override
	public Future<Void> send(Object request) {
		 Future<Channel> channelFuture = clientFuture.channelFuture();
		 if(channelFuture.isDone()) {
			 if(channelFuture.isSuccess()) {
				 return channelFuture.getNow().writeAndFlush(request);
			 }else {
				 return clientFuture.eventLoop().newFailedFuture(channelFuture.cause());
			 }
		 }else {
			 Promise<Void> promise = clientFuture.eventLoop().newPromise();
			 channelFuture.addListener(f->{
				 if(channelFuture.isSuccess()) {
					 ChannelFuture writeFuture = channelFuture.getNow().writeAndFlush(request);
					 writeFuture.addListener(f2->{
						 if(writeFuture.isSuccess()) {
							 promise.setSuccess(null);
						 }else {
							 promise.setFailure(writeFuture.cause());
						 }
					 });
				 }else {
					 promise.setFailure(channelFuture.cause());
				 }
			 });
			 return promise;
		 }
	}

	@Override
	public boolean isDone() {
		return clientFuture.future().isDone();
	}

	@Override
	public boolean isSuccess() {
		return clientFuture.future().isSuccess();
	}

	@Override
	public Future<Void> future() {
		return clientFuture.future();
	}

	@Override
	public Future<Channel> channelFuture() {
		return clientFuture.channelFuture();
	}

	
}
