package com.simplyti.service;

import com.simplyti.service.fileserver.FileServeConfiguration;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class ServerConfig {
	
	private final String name;
	private final int blockingThreadPool;
	private final int insecuredPort;
	private final int securedPort;
	private final FileServeConfiguration fileServer;
	private final boolean externalEventLoopGroup;
	private final boolean verbose;
	
	public ServerConfig(String name, int blockingThreadPool, int insecuredPort, int securedPort, FileServeConfiguration fileServer,
			boolean externalEventLoopGroup,boolean verbose){
		this.name=name;
		this.blockingThreadPool=blockingThreadPool;
		this.insecuredPort=insecuredPort;
		this.securedPort=securedPort;
		this.fileServer=fileServer;
		this.externalEventLoopGroup=externalEventLoopGroup;
		this.verbose=verbose;
	}
	
}
