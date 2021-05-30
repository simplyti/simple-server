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
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

@Sharable
public class EventDecoder<T extends K8sResource> extends ChannelInboundHandlerAdapter {
	
	private final Json json;
	private final TypeLiteral<Event<T>> eventType;

	public EventDecoder(Json json, TypeLiteral<Event<T>> eventType) {
		this.json=json;
		this.eventType=eventType;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if(msg instanceof ByteBuf) {
				ctx.fireChannelRead(channelRead0(ctx, (ByteBuf) msg));
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	private Event<T> channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
		Map<?,?> eventMap = json.deserialize(msg.slice(), Map.class);
		String typeStr = (String) eventMap.get("type");
		if(typeStr.equals(EventType.ERROR.name())) {
			msg.skipBytes(msg.readableBytes());
			String content = json.serializeAsString(eventMap.get("object"), CharsetUtil.UTF_8);
			Status status = json.deserialize(content, Status.class);
			throw new KubeClientException(status); 
		}else {
			return json.deserialize(msg,eventType);
		}
	}

}
