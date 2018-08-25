package com.simplyti.service.security.oidc.jwk;

import java.util.List;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class JsonWebKeys {
	
	private final List<JsonWebKey> keys;

	@JsonCreator
	public JsonWebKeys(
			@JsonProperty("keys") List<JsonWebKey> keys) {
		this.keys=keys;
	}

}
