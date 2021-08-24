package com.simplyti.service.clients.channel.pool;

import java.util.concurrent.TimeUnit;

import com.simplyti.service.clients.channel.handler.ReadTimeoutExceptionHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public abstract class AbstractClientPoolHandler extends AbstractChannelPoolHandler {
	
	private static final ChannelHandler READ_TIMEOUT_HANDLER = new ReadTimeoutExceptionHandler();
	
	private final long readTimeoutMillis;
	private final boolean verbose;

	public AbstractClientPoolHandler(long readTimeoutMillis, boolean verbose) {
		this.readTimeoutMillis=readTimeoutMillis;
		this.verbose=verbose;
	}
	
	@Override
	public void channelCreated(Channel ch) throws Exception {
		if(verbose) {
			ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
		}
		if(readTimeoutMillis >0) {
			ch.pipeline().addLast(new ReadTimeoutHandler(readTimeoutMillis, TimeUnit.MILLISECONDS));
			
		}
		channelCreated0(ch);
	}
	
	@Override
    public void channelAcquired(Channel ch) throws Exception {
		if(readTimeoutMillis >0 && ch.pipeline().get(ReadTimeoutExceptionHandler.class) != null) {
			ch.pipeline().remove(READ_TIMEOUT_HANDLER);
		}
    }

    @Override
    public void channelReleased(Channel ch) throws Exception {
    	if(readTimeoutMillis >0) {
    		ch.pipeline().addLast(READ_TIMEOUT_HANDLER);
    	}
    }

	protected abstract void channelCreated0(Channel ch);
}
