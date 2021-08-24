package com.simplyti.service.builder.di.guice;

public class DefaultListenerBuilder implements ListenerBuilder {
	
	private final Listenable listenable;

	public DefaultListenerBuilder(Listenable listenable) {
		this.listenable = listenable;
	}

	@Override
	public TcpListenerBuilder port(int port) {
		return new DefaultTcpListenerBuilder(listenable, port);
	}

	@Override
	public UnixDomainListenerBuilder unix(String file) {
		return new DefaultUnixDomainListenerBuilder(listenable, file);
	}

}
