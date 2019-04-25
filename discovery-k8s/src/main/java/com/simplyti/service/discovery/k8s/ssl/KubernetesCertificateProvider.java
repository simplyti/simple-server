package com.simplyti.service.discovery.k8s.ssl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.simplyti.service.clients.k8s.secrets.domain.Secret;
import com.simplyti.service.commons.ssl.CertificateUtils;
import com.simplyti.service.commons.ssl.PrivateKeyUtils;
import com.simplyti.service.ssl.ServerCertificate;
import com.simplyti.service.ssl.ServerCertificateProvider;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class KubernetesCertificateProvider implements ServerCertificateProvider{
	
	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	public final Map<String,SecretName> hostSecret = Maps.newConcurrentMap();
	public final Map<String,ServerCertificate> secrets = Maps.newConcurrentMap();

	@Override
	public ServerCertificate get(String alias) {
		SecretName secret = hostSecret.get(alias);
		if(secret!=null && secrets.containsKey(secret.name())) {
			return secrets.get(secret.name());
		}else {
			return null;
		}
	}

	public void add(String host, String secretId) {
		if(hostSecret.containsKey(host)) {
			hostSecret.get(host).retain();
		}else {
			hostSecret.put(host, new SecretName(secretId));
		}
	}

	public void remove(String host) {
		if(hostSecret.get(host).release().count()==0) {
			hostSecret.remove(host);
		}
	}

	public void addSecret(String secretId, Secret secret) {
		try {
			PrivateKey privateKey = PrivateKeyUtils.read(secret.data().get("tls.key").asString(CharsetUtil.UTF_8));
			List<X509Certificate> certificates = CertificateUtils.read(secret.data().get("tls.crt").asString(CharsetUtil.UTF_8));
			secrets.put(secretId, new ServerCertificate(certificates.stream().toArray(X509Certificate[]::new), privateKey));
		} catch (CertificateException | InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
			log.warn("Cannot create certificate and key",e);
		}
	}
	
	public void updateSecret(String secretId,Secret secret) {
		addSecret(secretId,secret);
	}

	public void removeSecret(String secretId) {
		secrets.remove(secretId);
	}
	
}
