package com.simplyti.service.clients.events;


public class ClientResponseEvent {

	private final Object response;

	public ClientResponseEvent(Object response) {
		this.response=response;
	}

	@SuppressWarnings("unchecked")
	public <T> T response() {
		return (T) response;
	}

}
