package com.simplyti.service.clients.channel.handler;

import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.util.concurrent.ScheduledFuture;

public class IdleTimeoutHandler implements ChannelPoolHandler {

	private final ChannelPoolHandler target;
	private final long timeoutMillis;
	
	private ScheduledFuture<ChannelFuture> schedule;

	public IdleTimeoutHandler(ChannelPoolHandler target, long timeoutMillis) {
		this.target=target;
		this.timeoutMillis=timeoutMillis;
	}

	@Override
	public void channelReleased(Channel ch) throws Exception {
		this.target.channelReleased(ch);
		this.schedule = ch.eventLoop().schedule(()->ch.close(), timeoutMillis, TimeUnit.MILLISECONDS);
	}

	@Override
	public void channelAcquired(Channel ch) throws Exception {
		if(schedule!=null) {
			schedule.cancel(true);
		}
		this.target.channelAcquired(ch);
		
	}

	@Override
	public void channelCreated(Channel ch) throws Exception {
		this.target.channelCreated(ch);
	}
	

}
