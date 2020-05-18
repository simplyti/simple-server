package com.simplyti.service;

import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ServerConfig {
	
	private final String name;
	private final int blockingThreadPool;
	private final Set<Listener> listeners;
	private final boolean externalEventLoopGroup;
	private final boolean verbose;
	
	public ServerConfig(String name, int blockingThreadPool, Set<Listener> listeners, 
			boolean externalEventLoopGroup,boolean verbose){
		this.name=name;
		this.blockingThreadPool=blockingThreadPool;
		this.listeners=listeners;
		this.externalEventLoopGroup=externalEventLoopGroup;
		this.verbose=verbose;
	}
	
}
