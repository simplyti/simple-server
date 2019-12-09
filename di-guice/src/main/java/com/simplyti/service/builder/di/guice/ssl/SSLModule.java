package com.simplyti.service.builder.di.guice.ssl;

import java.security.Provider;

import javax.inject.Singleton;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.OptionalBinder;
import com.simplyti.service.builder.di.SslContextProvider;
import com.simplyti.service.ssl.DefaultServerCertificateProvider;
import com.simplyti.service.ssl.DefaultSslHandlerFactory;
import com.simplyti.service.ssl.IoCKeyManager;
import com.simplyti.service.ssl.IoCKeyManagerFactory;
import com.simplyti.service.ssl.IoCKeyManagerFactorySpi;
import com.simplyti.service.ssl.IoCSecurityProvider;
import com.simplyti.service.ssl.IoCTrustManager;
import com.simplyti.service.ssl.IoCTrustManagerFactory;
import com.simplyti.service.ssl.IoCTrustManagerFactorySpi;
import com.simplyti.service.ssl.ServerCertificateProvider;
import com.simplyti.service.ssl.SslHandlerFactory;

import io.netty.handler.ssl.SslContext;

public class SSLModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(Provider.class).to(IoCSecurityProvider.class).in(Singleton.class);
		bind(KeyManager.class).to(IoCKeyManager.class).in(Singleton.class);
		bind(KeyManagerFactorySpi.class).to(IoCKeyManagerFactorySpi.class).in(Singleton.class);
		bind(KeyManagerFactory.class).to(IoCKeyManagerFactory.class).in(Singleton.class);
		bind(TrustManager.class).to(IoCTrustManager.class).in(Singleton.class);
		bind(TrustManagerFactorySpi.class).to(IoCTrustManagerFactorySpi.class).in(Singleton.class);
		bind(TrustManagerFactory.class).to(IoCTrustManagerFactory.class).in(Singleton.class);
		
		bind(SslContext.class).toProvider(SslContextProvider.class).in(Singleton.class);
		bind(SslHandlerFactory.class).to(DefaultSslHandlerFactory.class).in(Singleton.class);
		
		OptionalBinder.newOptionalBinder(binder(), DefaultServerCertificateProvider.class);
		OptionalBinder.newOptionalBinder(binder(), ServerCertificateProvider.class);
	}

}
