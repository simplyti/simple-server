package com.simplyti.service;

public class DefaultStartStopMonitor implements StartStopMonitor{

	private boolean stopping;

	@Override
	public boolean isStoping() {
		return stopping;
	}

	@Override
	public void stop() {
		this.stopping=true;
	}

}
