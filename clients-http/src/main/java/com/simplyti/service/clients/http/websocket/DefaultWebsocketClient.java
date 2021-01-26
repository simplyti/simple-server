package com.simplyti.service.clients.http.websocket;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.websocket.handler.WebSocketChannelHandler;
import com.simplyti.service.clients.request.ChannelProvider;
import com.simplyti.service.commons.netty.Promises;
import com.simplyti.service.commons.netty.pending.PendingMessages;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;

public class DefaultWebsocketClient implements WebsocketClient, Consumer<WebSocketFrame>{
	
	private final ChannelProvider channelProvider;
	private final EventExecutor executor;
	private final String uri;
	private final PendingMessages pendingMessage;
	private final Promise<Void> closeFuture;
	private final Promise<Void> connectFuture;
	
	private boolean initialized;
	private boolean failed;
	private ClientChannel channel;
	private Consumer<ByteBuf> consumer;

	public DefaultWebsocketClient(String uri, EventExecutor executor, ChannelProvider channelProvider) {
		this.channelProvider = channelProvider;
		this.executor=executor;
		this.uri=uri;
		this.pendingMessage=new PendingMessages();
		this.closeFuture = executor.newPromise();
		this.connectFuture = executor.newPromise();
	}
	
	@Override
	public Future<Void> send(String data) {
		initClient();
		return send(new TextWebSocketFrame(data));
	}

	private Future<Void> send(WebSocketFrame frame) {
		if(executor.inEventLoop()) {
			return send0(frame,null);
		} else {
			Promise<Void> promise = executor.newPromise();
			executor.execute(()->send0(frame,promise));
			return new DefaultFuture<>(promise, executor);
		}
		
	}

	private Future<Void> send0(WebSocketFrame frame, Promise<Void> promise) {
		if(failed) {
			frame.release();
			if(promise==null) {
				return new DefaultFuture<>(executor.newFailedFuture(this.connectFuture.cause()),executor);
			} else {
				promise.setFailure(this.connectFuture.cause());
				return null;
			}
		} else if(channel!=null) {
			if(promise==null) {
				return new DefaultFuture<>(channel.writeAndFlush(frame),channel.eventLoop());
			} else {
				Promises.toPromise(channel.writeAndFlush(frame), promise);
				return null;
			}
		} else {
			if(promise==null) {
				Promise<Void> writePromise = executor.newPromise();
				pendingMessage.pending(writePromise, frame);
				return new DefaultFuture<>(writePromise,executor);
			} else {
				pendingMessage.pending(promise, frame);
				return null;
			}
		}
	}

	@Override
	public WebsocketClient onMessage(Consumer<ByteBuf> consumer) {
		this.consumer=consumer;
		initClient();
		return this;
	}

	private void initClient() {
		if(this.initialized) {
			return;
		}
		if(executor.inEventLoop()) {
			initChannel0();
		} else {
			executor.execute(this::initChannel0);
		}
	}

	private void initChannel0() {
		if(this.initialized) {
			return;
		}
		this.initialized = true;
		channelProvider.channel()
			.thenCombine(channel->initChannel(channel))
			.thenAccept(this::connectionSuccess)
			.exceptionally(this::connectionFailure);
	}

	private void connectionSuccess(ClientChannel channel) {
		if(executor.inEventLoop()) {
			connectionSuccess0(channel);
		} else {
			executor.execute(()->connectionSuccess0(channel));
		}
	}
	
	private void connectionFailure(Throwable cause) {
		if(executor.inEventLoop()) {
			connectionFailure0(cause);
		} else {
			executor.execute(()->connectionFailure0(cause));
		}
	}

	private void connectionFailure0(Throwable cause) {
		this.failed=true;
		this.connectFuture.setFailure(cause);
		this.pendingMessage.fail(cause);
	}

	private void connectionSuccess0(ClientChannel channel) {
		this.channel=channel;
		this.connectFuture.setSuccess(null);
		this.pendingMessage.write(channel);
	}

	private io.netty.util.concurrent.Future<ClientChannel> initChannel(ClientChannel channel) {
		Promise<ClientChannel> promise = channel.eventLoop().newPromise();
		channel.pipeline().addLast(new WebSocketChannelHandler(uri,channel,promise, closeFuture, this));
		return promise;
	}

	@Override
	public void accept(WebSocketFrame t) {
		if(consumer!=null) {
			consumer.accept(t.content());
		}
	}

	@Override
	public Future<Void> closeFuture() {
		return new DefaultFuture<>(closeFuture,executor);
	}

	@Override
	public Future<Void> connectFuture() {
		return new DefaultFuture<>(connectFuture,executor);
	}
	
}
