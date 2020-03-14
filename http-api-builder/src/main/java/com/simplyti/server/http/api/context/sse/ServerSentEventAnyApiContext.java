package com.simplyti.server.http.api.context.sse;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.util.concurrent.Future;

public interface ServerSentEventAnyApiContext extends ApiContext {

	Future<Void> send(String data);

}
