package com.simplyti.service.ssl;

public interface ServerCertificateProvider {
	
	ServerCertificate get(String alias);

}
