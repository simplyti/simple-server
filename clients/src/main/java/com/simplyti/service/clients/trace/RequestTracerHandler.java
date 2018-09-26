package com.simplyti.service.clients.trace;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.simplyti.service.clients.ClientChannelEvent;
import com.simplyti.service.clients.events.ClientResponseEvent;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class RequestTracerHandler<I,O> extends ChannelDuplexHandler {

	private final RequestTracer<I,O> tracer;
	private final String uuid;
	
	private Instant start;

	public RequestTracerHandler(RequestTracer<I,O> tracer) {
		this.tracer=tracer;
		this.uuid = UUID.randomUUID().toString();
	}
	
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt==ClientChannelEvent.END_REQUEST) {
        		ctx.pipeline().remove(this);
        } else if (evt instanceof ClientResponseEvent) {
        		ClientResponseEvent resp = (ClientResponseEvent) evt;
	    		tracer.response(uuid,resp.response(),ChronoUnit.MILLIS.between(start, Instant.now()));
	    }
        ctx.fireUserEventTriggered(evt);
    }
	
	@SuppressWarnings("unchecked")
	@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		this.start=Instant.now();
		tracer.request(uuid,(I) msg);
        ctx.write(msg, promise);
    }
	
}
