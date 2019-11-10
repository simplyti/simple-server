package com.simplyti.service.security.oidc.key.resolver;

@SuppressWarnings("serial")
public class SigningKeyResolverException extends RuntimeException {
	
	public SigningKeyResolverException() { }

	public SigningKeyResolverException(Throwable cause) {
		super(cause);
	}

	public SigningKeyResolverException(String message) {
		super(message);
	}

}
