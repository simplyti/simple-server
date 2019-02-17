package com.simplyti.service.discovery.k8s.ssl;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.common.collect.Maps;
import com.simplyti.service.clients.k8s.secrets.domain.Secret;
import com.simplyti.service.ssl.PrivateKeyUtils;
import com.simplyti.service.ssl.ServerCertificate;
import com.simplyti.service.ssl.ServerCertificateProvider;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.vavr.control.Try;

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
		List<X509Certificate> certificates = new ArrayList<>();
		try(Scanner scanner = new Scanner(secret.data().get("tls.crt").asString(CharsetUtil.UTF_8))){
			StringBuilder builder = new StringBuilder();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(line.equals("-----BEGIN CERTIFICATE-----")) {
					builder.delete(0, builder.length());
				}else if(line.equals("-----END CERTIFICATE-----")) {
					ByteBuf certBuf = Base64.decode(Unpooled.wrappedBuffer(builder.toString().getBytes(CharsetUtil.UTF_8)));
					certificates.add(buildCertificate(new ByteBufInputStream(certBuf)));
					certBuf.release();
				}else {
					builder.append(line);
				}
			}
		}
		
		PrivateKey privateKey = Try.of(()->PrivateKeyUtils.read(secret.data().get("tls.key").asString(CharsetUtil.UTF_8)))
				.onFailure(log::error)
				.get();
		secrets.put(secretId, new ServerCertificate(certificates.stream().toArray(X509Certificate[]::new), privateKey));
	}
	
	private X509Certificate buildCertificate(InputStream input) {
		return (X509Certificate) Try.of(()->Try.of(()->CertificateFactory.getInstance("X.509")).get()
				.generateCertificate(input))
				.onFailure(log::error)
				.get();
	}

	public void updateSecret(String secretId,Secret secret) {
		addSecret(secretId,secret);
	}

	public void removeSecret(String secretId) {
		secrets.remove(secretId);
	}

	
	
}
