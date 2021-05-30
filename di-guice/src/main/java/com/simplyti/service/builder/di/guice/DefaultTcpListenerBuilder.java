package com.simplyti.service.builder.di.guice;

import com.simplyti.service.transport.tcp.TcpListener;

public class DefaultTcpListenerBuilder implements TcpListenerBuilder {

	private final Listenable listenable;
	private final int port;
	
	private boolean ssl;


	public DefaultTcpListenerBuilder(Listenable listenable, int port) {
		this.listenable=listenable;
		this.port=port;
	}

	@Override
	public ServiceBuilder end() {
		return listenable.listen(new TcpListener(port,ssl));
	}

	@Override
	public TcpListenerBuilder secured() {
		this.ssl=true;
		return this;
	}

}
