package com.simplyti.service.ssl.sni;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Optional;

import javax.inject.Inject;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.StandardConstants;
import javax.net.ssl.X509ExtendedKeyManager;

import com.google.common.base.Predicates;

public class SNIKeyManager extends X509ExtendedKeyManager {

	private static final String DEFAULT = "default";
	
	private final DefaultServerCertificateProvider defaultProvider;
	private final Optional<ServerCertificateProvider> certProvider;

	@Inject
	public SNIKeyManager(Optional<DefaultServerCertificateProvider> defaultProvider, Optional<ServerCertificateProvider> certProvider) {
		this.defaultProvider = defaultProvider.orElse(new SelfSignedServerCertificateProvider());
		this.certProvider=certProvider;
	}

	@Override
	public X509Certificate[] getCertificateChain(String alias) {
		return cert(alias).certificateChain();
	}

	@Override
	public PrivateKey getPrivateKey(String alias) {
		return cert(alias).privateKey();
	}

	private ServerCertificate cert(String alias) {
		if(certProvider.isPresent()) {
			ServerCertificate cert = certProvider.get().get(alias);
			if(cert==null) {
				return defaultProvider.get(alias);
			}else {
				return cert;
			}
		}else {
			return defaultProvider.get(alias);
		}
	}

	public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
		String hostname = retrieveHostName(engine);
		if (hostname != null) {
			return hostname;
		} else {
			return DEFAULT;
		}
    }
	
	private String retrieveHostName(SSLEngine engine) {
		ExtendedSSLSession session = (ExtendedSSLSession) engine.getHandshakeSession();
		return session.getRequestedServerNames().stream()
			.filter(name->Predicates.equalTo(StandardConstants.SNI_HOST_NAME).apply(name.getType()))
			.map(SNIHostName.class::cast)
			.findFirst()
			.map(name->name.getAsciiName()).orElse(null);
	}

	@Override
	public String[] getServerAliases(String keyType, Principal[] issuers) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String[] getClientAliases(String keyType, Principal[] issuers) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		throw new UnsupportedOperationException();
	}

}
