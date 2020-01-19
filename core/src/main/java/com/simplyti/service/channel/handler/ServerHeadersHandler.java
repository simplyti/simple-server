package com.simplyti.service.channel.handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import com.simplyti.service.ServerConfig;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.util.concurrent.FastThreadLocal;

@Sharable
public class ServerHeadersHandler extends ChannelOutboundHandlerAdapter {

	private final FastThreadLocal<SimpleDateFormat> dateFormatter = new FastThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() throws Exception {
			SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
			format.setTimeZone(TimeZone.getTimeZone("GMT"));
			return format;
		}
	};
	
	private final ServerConfig config;
	
	private String currentDate = null;
	private long dateValidUntil = 0;
	
	@Inject
	public ServerHeadersHandler(ServerConfig config) {
		this.config=config;
	}
	
	@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof HttpMessage) {
        	HttpMessage response = (HttpMessage) msg;
        	long time = System.currentTimeMillis();
        	String date;
        	if(isDateValid(time)) {
        		date = currentDate;
        	} else {
        		date = currentDate = newDate(time);
        	}
        	response.headers().set(HttpHeaderNames.DATE, date);
        	if(config.name()!=null) {
        		response.headers().set(HttpHeaderNames.SERVER,config.name());
        	}
        }
		ctx.write(msg, promise);
    }

	private String newDate(long time) {
		String value = dateFormatter.get().format(new Date(time));
		dateValidUntil = (time / 1000 + 1) * 1000;
		return value;
	}

	private boolean isDateValid(long time) {
		return time<dateValidUntil;
	}
	
}
