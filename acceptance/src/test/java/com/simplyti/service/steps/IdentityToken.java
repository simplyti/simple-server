package com.simplyti.service.steps;

import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class IdentityToken {
	
	@JsonProperty("access_token")
	private final String accessToken;
	@JsonProperty("token_type")
	private final String tokenType;
	@JsonProperty("id_token")
	private final String idToken;
	@JsonProperty("refresh_token")
	private final String refreshToken;
	@JsonProperty("expires_in")
	private final int expiresIn;
	
	@JsonCreator
	public IdentityToken(
			@JsonProperty("access_token") String accessToken,
			@JsonProperty("token_type") String tokenType,
			@JsonProperty("id_token") String idToken,
			@JsonProperty("refresh_token") String refreshToken,
			@JsonProperty("expires_in") int expiresIn) {
		this.accessToken=accessToken;
		this.tokenType=tokenType;
		this.idToken=idToken;
		this.refreshToken=refreshToken;
		this.expiresIn=expiresIn;
	}
	
	
}