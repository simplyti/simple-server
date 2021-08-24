package com.simplyti.service.clients.http.request;

import java.util.List;
import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.sse.domain.ServerEvent;
import com.simplyti.service.clients.http.sse.handler.HttpServerSentEventHandshakeHandled;
import com.simplyti.service.clients.http.sse.handler.ServerSentEventHandler;
import com.simplyti.service.clients.request.ChannelProvider;
import com.simplyti.service.clients.request.ClientRequestProvider;
import com.simplyti.service.filter.http.HttpRequestFilter;
import com.simplyti.util.concurrent.Future;

import io.netty.util.concurrent.Promise;

public class DefaultServerSentEventRequestBuilder implements ServerSentEventRequestBuilder {
	
	private final ChannelProvider channelProvider;
	private final ClientRequestProvider pending;
	private final Consumer<ChunckedBodyRequest> chunkedConsumer;
	private final List<HttpRequestFilter> filters;
	
	private Consumer<ClientChannel> connnectConsumer;

	public DefaultServerSentEventRequestBuilder(ChannelProvider channelProvider, Consumer<ChunckedBodyRequest> chunkedConsumer, ClientRequestProvider pending, boolean checkStatus, List<HttpRequestFilter> filters) {
		this.channelProvider=channelProvider;
		this.pending=pending;
		this.chunkedConsumer=chunkedConsumer;
		this.filters=filters;
	}

	@Override
	public Future<Void> forEach(Consumer<ServerEvent> consumer) {
		return channelProvider.channel()
				.thenCombine(channel->{
					channel.pipeline().fireUserEventTriggered(HttpServerSentEventHandshakeHandled.Event.START);
					if(this.connnectConsumer!=null) {
						this.connnectConsumer.accept(channel);
					}
					if(filters!=null) {
						channel.pipeline().fireUserEventTriggered(new HttpRequestFilterEvent(filters));
					}
					Promise<Void> promise = channel.eventLoop().newPromise();
					channel.pipeline().addLast(new ServerSentEventHandler(channel,null,promise,consumer));
					channel.writeAndFlush(pending.request(false,null)).addListener(f->hadleWriteFuture(f,channel,promise));
					return promise;
				});
	}
	
	private void hadleWriteFuture(io.netty.util.concurrent.Future<? super Void> future, ClientChannel channel, Promise<?> promise) {
		if(future.isSuccess()) {
			if(chunkedConsumer!=null) {
				chunkedConsumer.accept(new DefaultChunckedBodyRequest(channel));
			}
		} else {
			channel.close().addListener(f->channel.release());
			promise.tryFailure(future.cause());
		}
	}
	
	@Override
	public DefaultServerSentEventRequestBuilder onConnect(Consumer<ClientChannel> consumer) {
		this.connnectConsumer = consumer;
		return this;
	}

}
