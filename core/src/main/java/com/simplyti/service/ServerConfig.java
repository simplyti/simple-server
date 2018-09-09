package com.simplyti.service;

import com.simplyti.service.fileserver.FileServeConfiguration;

public class ServerConfig {
	
	private final Class<? extends Service<?>> serviceClass;
	private final int insecuredPort;
	private final int securedPort;
	private final FileServeConfiguration fileServer;
	private final boolean externalEventLoopGroup;
	
	public ServerConfig(Class<? extends Service<?>> serviceClass, int insecuredPort, int securedPort, FileServeConfiguration fileServer,
			boolean externalEventLoopGroup){
		this.serviceClass=serviceClass;
		this.insecuredPort=insecuredPort;
		this.securedPort=securedPort;
		this.fileServer=fileServer;
		this.externalEventLoopGroup=externalEventLoopGroup;
	}

	public int insecuredPort() {
		return insecuredPort;
	}
	
	public int securedPort() {
		return securedPort;
	}
	
	public Class<? extends Service<?>> serviceClass() {
		return serviceClass;
	}

	public FileServeConfiguration fileServe() {
		return fileServer;
	}
	
	
	public boolean externalEventLoopGroup() {
		return externalEventLoopGroup;
	}

}
