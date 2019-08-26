package com.simplyti.service.security.oidc.filter;


import com.simplyti.service.api.filter.FilterContext;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.security.oidc.handler.OpenIdHandler;

import io.netty.handler.codec.http.HttpRequest;

public class OpenIdRequestFilter extends AbstractOpenIdFilter<HttpRequest> implements HttpRequestFilter {

	public OpenIdRequestFilter(Json json, OpenIdHandler oidcConfig) {
		super(json,oidcConfig);
	}

	@Override
	protected HttpRequest request(FilterContext<HttpRequest> context) {
		return context.object();
	}
	
}
