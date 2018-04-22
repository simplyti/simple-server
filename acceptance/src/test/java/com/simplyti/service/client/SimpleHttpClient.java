package com.simplyti.service.client;

import javax.security.cert.X509Certificate;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.Future;

public interface SimpleHttpClient {

	Future<SimpleHttpResponse> get(HttpVersion version,String path,int port, boolean ssl, String sni, HttpHeaders headers);

	Future<SimpleHttpResponse> post(String path, String body);
	
	Future<SimpleHttpResponse> delete( String path);
	
	Future<SimpleHttpResponse> send(Object... obj);
	
	Future<SimpleHttpResponse> sendPort(String host,int port, boolean ssl, Object... obj);
	
	Future<SimpleHttpResponse> sendPort(String host,String sni,int port, boolean ssl, Object... obj);

	int activeConnections();

	X509Certificate lastServerCertificate();

}
