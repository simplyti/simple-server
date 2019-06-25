package com.simplyti.service;

import com.simplyti.service.fileserver.FileServeConfiguration;

public class ServerConfig {
	
	private final int insecuredPort;
	private final int securedPort;
	private final FileServeConfiguration fileServer;
	private final boolean externalEventLoopGroup;
	private final boolean verbose;
	
	public ServerConfig(int insecuredPort, int securedPort, FileServeConfiguration fileServer,
			boolean externalEventLoopGroup,boolean verbose){
		this.insecuredPort=insecuredPort;
		this.securedPort=securedPort;
		this.fileServer=fileServer;
		this.externalEventLoopGroup=externalEventLoopGroup;
		this.verbose=verbose;
	}

	public int insecuredPort() {
		return insecuredPort;
	}
	
	public int securedPort() {
		return securedPort;
	}
	
	public FileServeConfiguration fileServe() {
		return fileServer;
	}
	
	
	public boolean externalEventLoopGroup() {
		return externalEventLoopGroup;
	}

	public boolean verbose() {
		return verbose;
	}

}
