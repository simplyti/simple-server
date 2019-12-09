package com.simplyti.service.builder.di.dagger.ssl;

import java.security.Provider;
import java.security.cert.CertificateException;
import java.util.Optional;

import javax.inject.Singleton;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;

import com.simplyti.service.builder.di.SslContextProvider;
import com.simplyti.service.ssl.DefaultSslHandlerFactory;
import com.simplyti.service.ssl.IoCKeyManager;
import com.simplyti.service.ssl.IoCKeyManagerFactory;
import com.simplyti.service.ssl.IoCKeyManagerFactorySpi;
import com.simplyti.service.ssl.IoCSecurityProvider;
import com.simplyti.service.ssl.IoCTrustManager;
import com.simplyti.service.ssl.IoCTrustManagerFactory;
import com.simplyti.service.ssl.IoCTrustManagerFactorySpi;
import com.simplyti.service.ssl.SslHandlerFactory;

import dagger.Module;
import dagger.Provides;
import io.netty.handler.ssl.SslContext;

@Module(includes= { SSLOptionals.class })
public class SSLModule {

	@Provides
	@Singleton
	public Provider provider() {
		return new IoCSecurityProvider();
	}

	@Provides
	@Singleton
	public KeyManager keyManager() {
		try {
			return new IoCKeyManager(Optional.empty(), Optional.empty());
		} catch (CertificateException e) {
			throw new IllegalStateException(e);
		}
	}

	@Provides
	@Singleton
	public KeyManagerFactorySpi keyManagerFactorySpi(KeyManager manager) {
		return new IoCKeyManagerFactorySpi(manager);
	}

	@Provides
	@Singleton
	public KeyManagerFactory keyManagerFactory(KeyManagerFactorySpi spi, Provider provider) {
		return new IoCKeyManagerFactory(spi, provider);
	}

	@Provides
	@Singleton
	public TrustManagerFactorySpi trustManagerFactorySpi(TrustManager trustManager) {
		return new IoCTrustManagerFactorySpi(trustManager);
	}

	@Provides
	@Singleton
	public TrustManagerFactory trustManagerFactory(TrustManagerFactorySpi spi, Provider provider) {
		return new IoCTrustManagerFactory(spi, provider);
	}

	@Provides
	@Singleton
	public TrustManager trustManager() {
		return new IoCTrustManager();
	}

	@Provides
	@Singleton
	public SslContext sslContext(KeyManagerFactory keyManagerFactory, TrustManagerFactory trustManagerFactory) {
		return new SslContextProvider(keyManagerFactory, trustManagerFactory).get();
	}

	@Provides
	@Singleton
	public SslHandlerFactory sslHandlerFactory(SslContext sslCtx) {
		return new DefaultSslHandlerFactory(sslCtx);
	}

}
