package com.simplyti.service.clients.monitor;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

public class DefaultClientMonitor implements ClientMonitor, ClientMonitorHandler {
	
	private final ChannelGroup allChannels;
	private final ChannelGroup activeChannels;
	private final ChannelGroup iddleChannels;
	
	public DefaultClientMonitor(EventLoopGroup eventLoopgroup) {
		EventLoop channelGroupsEventLoop = eventLoopgroup.next();
		this.allChannels=new DefaultChannelGroup(channelGroupsEventLoop);
		this.activeChannels=new DefaultChannelGroup(channelGroupsEventLoop);
		this.iddleChannels=new DefaultChannelGroup(channelGroupsEventLoop);
	}

	public int idleConnections() {
		return iddleChannels.size();
	}

	public int activeConnections() {
		return activeChannels.size();
	}

	public int totalConnections() {
		return allChannels.size();
	}

	public void released(Channel ch) {
		activeChannels.remove(ch);
		iddleChannels.add(ch);
	}

	public void acquired(Channel ch) {
		iddleChannels.remove(ch);
		activeChannels.add(ch);
	}

	public void created(Channel ch) {
		allChannels.add(ch);
		activeChannels.add(ch);
	}

	@Override
	public ChannelGroup idleChannels() {
		return iddleChannels;
	}

}
