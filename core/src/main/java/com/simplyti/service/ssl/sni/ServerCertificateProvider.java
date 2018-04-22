package com.simplyti.service.ssl.sni;

public interface ServerCertificateProvider {
	
	ServerCertificate get(String alias);

}
