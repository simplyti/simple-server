package com.simplyti.service.clients.http.sse;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.sse.handler.ServerEventResponseHandler;
import com.simplyti.service.clients.stream.PendingRequest;
import com.simplyti.util.concurrent.Future;

import io.netty.util.concurrent.Promise;

public class DefaultServerSentEvents implements ServerSentEvents {
	
	private static final String HANDLER = "handler";

	private final PendingRequest pendingRequest;
	
	private Future<ClientChannel> futureChannel;

	public DefaultServerSentEvents(PendingRequest pendingRequest) {
		this.pendingRequest = pendingRequest;
	}

	@Override
	public Future<Void> onEvent(Consumer<ServerEvent> consumer) {
		this.futureChannel = pendingRequest.channel();
		return this.futureChannel.thenCombine(ch->pendingRequest.addHandlerAndSend(this.futureChannel, ()->eventHandle(ch,consumer)));
	}
	
	private io.netty.util.concurrent.Future<Void> eventHandle(ClientChannel ch, Consumer<ServerEvent> consumer){
		Promise<Void> promise = ch.eventLoop().newPromise();
		ch.pipeline().addLast(HANDLER,new ServerEventResponseHandler(HANDLER,promise,consumer));
		return promise;
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
