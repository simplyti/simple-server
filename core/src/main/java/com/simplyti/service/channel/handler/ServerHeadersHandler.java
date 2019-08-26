package com.simplyti.service.channel.handler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.inject.Inject;

import com.simplyti.service.ServerConfig;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMessage;
import lombok.AllArgsConstructor;

@Sharable
@AllArgsConstructor(onConstructor=@__(@Inject))
public class ServerHeadersHandler extends ChannelOutboundHandlerAdapter {

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
	
	private final ServerConfig config;
	
	@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof HttpMessage) {
        	HttpMessage response = (HttpMessage) msg;
        	ZonedDateTime now = LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("GMT"));
        	response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(now));
        	if(config.name()!=null) {
        		response.headers().set(HttpHeaderNames.SERVER,config.name());
        	}
        }
		ctx.write(msg, promise);
    }
	
}
