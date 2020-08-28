package com.simplyti.server.http.api.context.stream;

import com.simplyti.util.concurrent.Future;

public interface StreamedResponseContext {

	Future<Void> send(String data);

	Future<Void> finish();

}
