package com.simplyti.service.clients.http.request;

import java.util.List;
import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.HttpClientStreamEvent;
import com.simplyti.service.clients.http.handler.ClientChannelInitializer;
import com.simplyti.service.clients.http.handler.StreamedUnsafeTypeResponseHandler;
import com.simplyti.service.clients.request.ChannelProvider;
import com.simplyti.service.clients.request.ClientRequestProvider;
import com.simplyti.service.filter.http.HttpRequestFilter;
import com.simplyti.util.concurrent.Future;

import io.netty.util.concurrent.Promise;

public class UnsafeTypeStreamedHandledHttpRequestBuilder<T> implements StreamedFinalHandledHttpRequestBuilder<T> {

	private final ChannelProvider channelProvider;
	private final ClientRequestProvider pending;
	private final ClientChannelInitializer initializer;
	private final boolean checkStatus;
	private final List<HttpRequestFilter> filters;
	private final Consumer<ClientChannel> connnectConsumer;

	public UnsafeTypeStreamedHandledHttpRequestBuilder(ClientChannelInitializer initializer, ChannelProvider channelProvider, ClientRequestProvider pending, Consumer<ClientChannel> connnectConsumer, boolean checkStatus, List<HttpRequestFilter> filters) {
		this.channelProvider=channelProvider;
		this.pending=pending;
		this.initializer=initializer;
		this.checkStatus=checkStatus;
		this.connnectConsumer=connnectConsumer;
		this.filters=filters;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Future<Void> forEach(Consumer<T> consumer) {
		return channelProvider.channel()
				.thenCombine(channel->{
					channel.pipeline().fireUserEventTriggered(new HttpClientStreamEvent(HttpClientStreamEvent.Type.START,checkStatus));
					if(this.connnectConsumer!=null) {
						this.connnectConsumer.accept(channel);
					}
					if(filters!=null) {
						channel.pipeline().fireUserEventTriggered(new HttpRequestFilterEvent(filters));
					}
					Promise<Void> promise = channel.eventLoop().newPromise();
					StreamedUnsafeTypeResponseHandler handler = new StreamedUnsafeTypeResponseHandler(channel, null, promise, (Consumer<Object>) consumer);
					initializer.init(handler);
					channel.pipeline().addLast(handler);
					channel.writeAndFlush(pending.request(false,null)).addListener(f->hadleWriteFuture(f,channel,promise));
					return promise;
				});
	}
	
	private void hadleWriteFuture(io.netty.util.concurrent.Future<? super Void> future, ClientChannel channel, Promise<?> promise) {
		if(!future.isSuccess()) {
			channel.close().addListener(f->channel.release());
			promise.tryFailure(future.cause());
		}
	}

}
