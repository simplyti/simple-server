package com.simplyti.service.clients.monitor;

public interface ClientMonitor {
	
	public int totalConnections();
	public int activeConnections();
	public int idleConnections();

}
