package com.simplyti.service.security.oidc.handler;

import java.security.Key;

import io.netty.handler.codec.http.HttpRequest;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent=true)
public class DefaultRedirectableOpenIdHandler extends DefaultOpenIdHandler implements RedirectableOpenIdHandler {

	private final String authorizationEndpoint;
	
	@Getter
	private final String callbackUri;
	
	@Getter
	private final String clientId;

	public DefaultRedirectableOpenIdHandler(Key key, String authorizationEndpoint,String callbackUri,String clientId) {
		super(key);
		this.authorizationEndpoint=authorizationEndpoint;;
		this.callbackUri=callbackUri;
		this.clientId=clientId;
	}

	@Override
	public String getAuthorizationUrl(HttpRequest request) {
		String clientId = clientId();
		if(clientId==null) {
			return null;
		}
		
		String state = state(request);
		String endpoint = authorizationEndpoint()
				+ "?redirect_uri="+callbackUri(request)
				+ "&response_type=code&client_id=" + clientId
	            + "&scope=openid+email+profile+groups"
	            + "&approval_prompt=force"
	            + "&access_type=offline";
		if(state!=null) {
			endpoint+="&state="+state;
		}
		return endpoint;
	}

	protected String authorizationEndpoint() {
		return authorizationEndpoint;
	}

	protected String state(HttpRequest request) {
		return null;
	}

	protected String callbackUri(HttpRequest request) {
		return callbackUri;
	}


}
