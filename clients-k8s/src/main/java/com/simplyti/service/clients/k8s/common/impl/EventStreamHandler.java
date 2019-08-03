package com.simplyti.service.clients.k8s.common.impl;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.watch.Observable;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.json.JsonObjectDecoder;

public class EventStreamHandler<T extends K8sResource> extends SimpleChannelInboundHandler<ByteBuf> {
	
	public static final String NAME = "evt-handler";
	
	private final Json json;
	
	private final Observable<T> observable;
	private final TypeLiteral<Event<T>> eventType;


	public EventStreamHandler(Json json,Observable<T> observable, TypeLiteral<Event<T>> eventType) {
		this.json=json;
		this.observable=observable;
		this.eventType=eventType;
	}
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		observable.channel(ctx.channel());
		ctx.pipeline().addBefore(NAME, "json-decoder", new JsonObjectDecoder());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		observable.event(response(msg));
	}
	
	protected Event<T> response(ByteBuf content) {
		return json.deserialize(content,eventType);
	}

}
