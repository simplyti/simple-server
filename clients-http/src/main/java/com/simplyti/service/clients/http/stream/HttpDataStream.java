package com.simplyti.service.clients.http.stream;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.handler.ErrorHandler;
import com.simplyti.service.clients.http.stream.handler.StreamObjectResponseHandler;
import com.simplyti.service.clients.http.stream.handler.StreamResponseHandler;
import com.simplyti.service.clients.stream.PendingRequest;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.concurrent.Promise;

public class HttpDataStream implements HttpInputStream {

	private final PendingRequest pendingRequest;
	
	private Future<ClientChannel> futureChannel;
	
	public HttpDataStream(PendingRequest pendingRequest) {
		this.pendingRequest=pendingRequest;
	}

	@Override
	public Future<Void> onData(Consumer<ByteBuf> consumer) {
		this.futureChannel =  pendingRequest.channel();
		return this.futureChannel.thenCombine(ch->pendingRequest.addHandlerAndSend(this.futureChannel, ()->streamHandle(ch,consumer)));
	}
	
	private io.netty.util.concurrent.Future<Void> streamHandle(ClientChannel ch, Consumer<ByteBuf> consumer){
		Promise<Void> promise = ch.eventLoop().newPromise();
		ch.pipeline().addLast(new StreamResponseHandler(ch,promise,originalHandlers(ch),consumer));
		ch.pipeline().addLast(new ErrorHandler(promise));
		return promise;
	}
	
	@Override
	public Future<Void> forEach(Consumer<HttpObject> consumer) {
		this.futureChannel =  pendingRequest.channel();
		return this.futureChannel.thenCombine(ch->pendingRequest.addHandlerAndSend(this.futureChannel, ()->streamObjectsHandle(ch,consumer)));
	}
	
	private io.netty.util.concurrent.Future<Void> streamObjectsHandle(ClientChannel ch, Consumer<HttpObject> consumer){
		Promise<Void> promise = ch.eventLoop().newPromise();
		ch.pipeline().addLast(new StreamObjectResponseHandler(ch,promise,originalHandlers(ch),consumer));
		ch.pipeline().addLast(new ErrorHandler(promise));
		return promise;
	}
	
	@Override
	public Future<Void> withHandler(Consumer<ClientChannel> initialized) {
		this.futureChannel =  pendingRequest.channel();
		return this.futureChannel.thenCombine(ch->pendingRequest.addHandlerAndSend(this.futureChannel, ()->customStreamHandler(ch,initialized)));
	}

	private io.netty.util.concurrent.Future<Void> customStreamHandler(ClientChannel ch, Consumer<ClientChannel> initialized) {
		Promise<Void> promise = ch.eventLoop().newPromise();
		ch.pipeline().addLast(new StreamResponseHandler(ch,promise,originalHandlers(ch),null));
		initialized.accept(ch);
		ch.pipeline().addLast(new ErrorHandler(promise));
		return promise;
	}
	
	private List<String> originalHandlers(ClientChannel channel) {
		return StreamSupport.stream(channel.pipeline().spliterator(),false)
			.map(Entry::getKey)
			.collect(Collectors.toList());
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
