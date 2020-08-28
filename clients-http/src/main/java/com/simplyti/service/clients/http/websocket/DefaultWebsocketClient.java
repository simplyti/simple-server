package com.simplyti.service.clients.http.websocket;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.websocket.handler.WebSocketChannelHandler;
import com.simplyti.service.clients.request.ChannelProvider;
import com.simplyti.service.clients.stream.StreamedOutput;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;

public class DefaultWebsocketClient implements WebsocketClient {
	
	private final ChannelProvider channelProvider;
	private final EventExecutor executor;
	private final String uri;
	
	private Future<ClientChannel> futureChannel;
	private StreamedOutput streamOutput;

	public DefaultWebsocketClient(String uri,ChannelProvider channelProvider, EventExecutor executor) {
		this.channelProvider = channelProvider;
		this.executor=executor;
		this.uri=uri;
	}
	
	@Override
	public Future<Void> send(String data) {
		return streamOutput.send(new TextWebSocketFrame(data));
	}

	@Override
	public Future<Void> onData(Consumer<ByteBuf> consumer) {
		Promise<Void> promise = executor.newPromise();
		this.futureChannel = channelProvider.channel();
		Future<ClientChannel> handshackedChannel = futureChannel.thenCombine(ch->handshake(ch,consumer,promise));
		this.streamOutput = new StreamedOutput(handshackedChannel, executor);
		return handshackedChannel.thenCombine(ch->promise);
	}
	
	private io.netty.util.concurrent.Future<ClientChannel> handshake(ClientChannel channel, Consumer<ByteBuf> consumer, Promise<Void> promise) {
		Promise<ClientChannel> handshakeFuture = channel.eventLoop().newPromise();
		channel.pipeline().addLast(new HttpObjectAggregator(65536));
		channel.pipeline().addLast(new WebSocketChannelHandler(uri,channel,handshakeFuture,consumer,promise));
		return handshakeFuture;
	}

	@Override
	public void close() {
		if(futureChannel!=null) {
			if(futureChannel.isDone()) {
				futureChannel.getNow().close();
			} else {
				futureChannel.addListener(f->{
					if(f.isSuccess()) {
						futureChannel.getNow().close();
					}
				});
			}
		} else {
			throw new IllegalStateException("Channel is not requested yet. Use onData method first");
		}
	}

}
