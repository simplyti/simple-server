package com.simplyti.service;

import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ServerConfig {
	
	private final String name;
	private final int blockingThreadPool;
	private final List<Listener> listeners;
	private final boolean externalEventLoopGroup;
	private final boolean verbose;
	
	public ServerConfig(String name, int blockingThreadPool, List<Listener> listeners, 
			boolean externalEventLoopGroup,boolean verbose){
		this.name=name;
		this.blockingThreadPool=blockingThreadPool;
		this.listeners=listeners;
		this.externalEventLoopGroup=externalEventLoopGroup;
		this.verbose=verbose;
	}
	
}
