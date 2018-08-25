package com.simplyti.service.security.oidc.filter;


import com.simplyti.service.api.filter.FilterContext;
import com.simplyti.service.api.filter.HttpRequetFilter;
import com.simplyti.service.security.oidc.handler.OpenIdHandler;

import io.netty.handler.codec.http.HttpRequest;

public class OpenIdRequestFilter extends AbstractOpenIdFilter<HttpRequest> implements HttpRequetFilter {

	public OpenIdRequestFilter(OpenIdHandler oidcConfig) {
		super(oidcConfig);
	}

	@Override
	protected HttpRequest request(FilterContext<HttpRequest> context) {
		return context.object();
	}
	
}
