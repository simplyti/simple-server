package com.simplyti.service.clients.monitor;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

public interface ClientMonitorHandler {
	
	public void released(Channel ch);
	public void acquired(Channel ch);
	public void created(Channel ch);
	
	public ChannelGroup idleChannels();

}
