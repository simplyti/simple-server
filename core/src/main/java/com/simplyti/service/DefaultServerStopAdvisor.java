package com.simplyti.service;

public class DefaultServerStopAdvisor implements ServerStopAdvisor {
	
	private boolean stopping;

	@Override
	public boolean isStoping() {
		return stopping;
	}

	@Override
	public void stopAdvice() {
		this.stopping=true;
	}

}
