package com.simplyti.service.security.oidc.jwk;

import java.util.List;


import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class JsonWebKeys {
	
	private final List<JsonWebKey> keys;

	public JsonWebKeys(List<JsonWebKey> keys) {
		this.keys=keys;
	}

}
