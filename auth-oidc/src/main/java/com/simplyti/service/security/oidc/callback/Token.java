package com.simplyti.service.security.oidc.callback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent=true)
@Getter
@AllArgsConstructor
public class Token {
	
	private final String id_token;
	private final int expires_in;

}
