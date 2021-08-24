package com.simplyti.service.builder.di.guice;

import com.simplyti.service.transport.unix.UnixDomainListener;

public class DefaultUnixDomainListenerBuilder implements UnixDomainListenerBuilder {

	private final  Listenable listenable;
	private final String file;
	
	private boolean ssl;

	public DefaultUnixDomainListenerBuilder(Listenable listenable, String file) {
		this.listenable = listenable;
		this.file = file;
	}

	@Override
	public ServiceBuilder end() {
		return listenable.listen(new UnixDomainListener(file, ssl));
	}

	@Override
	public UnixDomainListenerBuilder ssl() {
		this.ssl=true;
		return this;
	}

}
