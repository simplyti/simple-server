package com.simplyti.service.builder.di.dagger;

import java.util.Set;

import javax.inject.Inject;

import com.simplyti.service.DefaultService;
import com.simplyti.service.ServerConfig;
import com.simplyti.service.StartStopMonitor;
import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.concurrent.Future;

public class DaggerService {


	private final EventLoopGroup eventLoopGroup;
	private final StartStopMonitor startStopMonitor;
	private final ServiceChannelInitializer serviceChannelInitializer;
	private final EventLoop startStopLoop;
	private final ClientChannelGroup clientChannelGroup;
	private final ChannelFactory<ServerChannel> channelFactory;
	private final Set<ServerStartHook> serverStartHook;
	private final Set<ServerStopHook> serverStopHook;
	private final ServerConfig config;

	@Inject
	public DaggerService(EventLoopGroup eventLoopGroup, StartStopMonitor startStopMonitor, @StartStopLoop EventLoop startStopLoop,
			ServiceChannelInitializer serviceChannelInitializer, ClientChannelGroup clientChannelGroup, ServerConfig config,
			ChannelFactory<ServerChannel> channelFactory,Set<ServerStartHook> serverStartHook, Set<ServerStopHook> serverStopHook) {
		this.eventLoopGroup=eventLoopGroup;
		this.startStopMonitor=startStopMonitor;
		this.serviceChannelInitializer=serviceChannelInitializer;
		this.startStopLoop=startStopLoop;
		this.clientChannelGroup=clientChannelGroup;
		this.channelFactory=channelFactory;
		this.serverStartHook=serverStartHook;
		this.serverStopHook=serverStopHook;
		this.config=config;
	}

	public Future<DefaultService> start() {
		DefaultService service = new DefaultService(eventLoopGroup, startStopMonitor, serviceChannelInitializer, startStopLoop, config, clientChannelGroup, channelFactory, serverStartHook, serverStopHook);
		return service.start();
	}

}