package com.simplyti.service.clients.channel.monitor;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;

public class MonitoredHandler implements ChannelPoolHandler {

	private final ChannelPoolHandler target;
	private final ClientMonitorHandler monitorHandler;
	
	public MonitoredHandler(ClientMonitorHandler monitorHandler, ChannelPoolHandler target) {
		this.target=target;
		this.monitorHandler=monitorHandler;
	}

	@Override
	public void channelReleased(Channel ch) throws Exception {
		monitorHandler.released(ch);
		target.channelReleased(ch);
	}

	@Override
	public void channelAcquired(Channel ch) throws Exception {
		monitorHandler.acquired(ch);
		target.channelAcquired(ch);
	}

	@Override
	public void channelCreated(Channel ch) throws Exception {
		monitorHandler.created(ch);
		target.channelCreated(ch);
	}

}
