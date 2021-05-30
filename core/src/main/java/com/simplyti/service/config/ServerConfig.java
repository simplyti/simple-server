package com.simplyti.service.config;

import java.util.Collection;

import com.simplyti.service.transport.Listener;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ServerConfig {
	
	private final String name;
	private final int blockingThreadPool;
	private final Collection<Listener> listeners;
	private final boolean externalEventLoopGroup;
	private final boolean verbose;
	private final int maxBodySize;
	
	public ServerConfig(String name, int blockingThreadPool, Collection<Listener> listeners, 
			boolean externalEventLoopGroup,boolean verbose, int maxBodySize){
		this.name=name;
		this.blockingThreadPool=blockingThreadPool;
		this.listeners=listeners;
		this.externalEventLoopGroup=externalEventLoopGroup;
		this.verbose=verbose;
		this.maxBodySize=maxBodySize;
	}
	
}
