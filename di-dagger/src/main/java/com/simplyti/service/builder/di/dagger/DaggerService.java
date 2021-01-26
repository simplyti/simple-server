package com.simplyti.service.builder.di.dagger;

import javax.inject.Inject;

import com.simplyti.service.Server;

import io.netty.util.concurrent.Future;

public class DaggerService {

	private Server server;

	@Inject
	public DaggerService(Server server) {
		this.server=server;
	}

	public Future<Server> start() {
		return server.start();
	}

}