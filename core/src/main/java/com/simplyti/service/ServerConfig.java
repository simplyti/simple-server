package com.simplyti.service;

import com.simplyti.service.fileserver.FileServeConfiguration;

public class ServerConfig {
	
	private final int insecuredPort;
	private final int securedPort;
	private final FileServeConfiguration fileServer;
	
	public ServerConfig(int insecuredPort, int securedPort, FileServeConfiguration fileServer){
		this.insecuredPort=insecuredPort;
		this.securedPort=securedPort;
		this.fileServer=fileServer;
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

}
