package com.simplyti.service.clients.channel.monitor;

import io.netty.channel.Channel;

public interface ClientMonitorHandler {
	
	public void released(Channel ch);
	public void acquired(Channel ch);
	public void created(Channel ch);

}
