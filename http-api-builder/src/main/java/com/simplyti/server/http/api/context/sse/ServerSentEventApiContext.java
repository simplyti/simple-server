package com.simplyti.server.http.api.context.sse;

import com.simplyti.util.concurrent.Future;

public interface ServerSentEventApiContext {

	Future<Void> send(String string);

}
