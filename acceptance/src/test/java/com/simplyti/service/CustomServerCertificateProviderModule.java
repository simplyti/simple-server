package com.simplyti.service;

import java.security.cert.X509Certificate;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.simplyti.service.ssl.sni.ServerCertificate;
import com.simplyti.service.ssl.sni.ServerCertificateProvider;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.vavr.control.Try;

public class CustomServerCertificateProviderModule extends AbstractModule implements ServerCertificateProvider{
	
	private static final String NAME = "example.com";
	
	private ServerCertificate serverCertificate;

	public CustomServerCertificateProviderModule() {
		SelfSignedCertificate ssc = Try.of(()->new SelfSignedCertificate(NAME)).get();
		this.serverCertificate = new ServerCertificate(new X509Certificate[] {ssc.cert()}, ssc.key());
	}
	
	@Override
	protected void configure() {
		bind(ServerCertificateProvider.class).to(CustomServerCertificateProviderModule.class).in(Singleton.class);
	}

	@Override
	public ServerCertificate get(String alias) {
		if(alias.equals(NAME)) {
			return serverCertificate;
		}else {
			return null;
		}
	}

}
