package com.simplyti.server.http.api.builder.sse;

public interface ServerSentEventApiBuilder {
	
	void then(ServerSentEventAnyApiContextConsumer consumer);

}
