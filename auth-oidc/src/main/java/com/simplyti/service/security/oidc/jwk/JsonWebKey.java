package com.simplyti.service.security.oidc.jwk;

import java.security.Key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@AllArgsConstructor
public class JsonWebKey {
	
	private final String alg;
	private final String kid;
	private final Key key;

}