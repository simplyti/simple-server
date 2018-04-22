package com.simplyti.service.clients.channel.monitor;

public interface ClientMonitor {
	
	public int totalConnections();
	public int activeConnections();
	public int iddleConnections();

}
