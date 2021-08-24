package com.simplyti.service.clients.http.request;

import com.simplyti.util.concurrent.Future;

import io.netty.util.concurrent.EventExecutor;

public interface ChunckedBodyRequest {

	Future<Void> send(String data);

	Future<Void> end();

	EventExecutor executor();

}
