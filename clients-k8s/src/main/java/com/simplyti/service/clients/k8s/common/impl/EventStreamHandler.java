package com.simplyti.service.clients.k8s.common.impl;

import java.util.Map;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.domain.KubeClientException;
import com.simplyti.service.clients.k8s.common.domain.Status;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.common.watch.domain.EventType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.CharsetUtil;

public class EventStreamHandler<T extends K8sResource> extends SimpleChannelInboundHandler<ByteBuf> {
	
	public static final String EVENT_HANDLER = "evt-handler";
	
	private final Json json;
	
	private final String handlerName;
	private final InternalObservable<T> observable;
	private final TypeLiteral<Event<T>> eventType;

	public EventStreamHandler(String handlerName, Json json,InternalObservable<T> observable, TypeLiteral<Event<T>> eventType) {
		this.handlerName=handlerName;
		this.json=json;
		this.observable=observable;
		this.eventType=eventType;
	}
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		observable.channel(ctx.channel());
		ctx.pipeline().addBefore(handlerName, "json-decoder", new JsonObjectDecoder());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		Map<?,?> eventMap = json.deserialize(msg.slice(), Map.class);
		String typeStr = (String) eventMap.get("type");
		if(typeStr.equals(EventType.ERROR.name())) {
			msg.skipBytes(msg.readableBytes());
			String content = json.serializeAsString(eventMap.get("object"), CharsetUtil.UTF_8);
			Status status = json.deserialize(content, Status.class);
			throw new KubeClientException(status); 
		}else {
			Event event = json.deserialize(msg,eventType);
			observable.event(event);
		}
		
	}

}
