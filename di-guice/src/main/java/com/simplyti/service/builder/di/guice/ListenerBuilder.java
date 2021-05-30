package com.simplyti.service.builder.di.guice;

public interface ListenerBuilder {

	TcpListenerBuilder port(int port);

	UnixDomainListenerBuilder unix(String file);

}
