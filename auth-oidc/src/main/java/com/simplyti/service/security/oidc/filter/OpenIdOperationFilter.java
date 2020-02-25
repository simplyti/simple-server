package com.simplyti.service.security.oidc.filter;

import javax.inject.Inject;

import com.simplyti.server.http.api.filter.OperationInboundFilter;
import com.simplyti.server.http.api.handler.ApiInvocation;
import com.simplyti.service.api.filter.FilterContext;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.security.oidc.handler.OpenIdHandler;

import io.netty.handler.codec.http.HttpRequest;

public class OpenIdOperationFilter extends AbstractOpenIdFilter<ApiInvocation> implements OperationInboundFilter{
	
	public static final String META_ATT = "auth.oidc";
	
	@Inject
	public OpenIdOperationFilter(Json json, OpenIdHandler oidcConfig) {
		super(json,oidcConfig);
	}

	@Override
	public void execute(FilterContext<ApiInvocation> context) {
		if(!isEnabled(context)) {
			context.done();
		}else {
			super.execute(context);
		}
	}

	private boolean isEnabled(FilterContext<ApiInvocation> context) {
		Object oidc = context.object().match().operation().meta(META_ATT);
		return oidc!=null && oidc instanceof String && Boolean.parseBoolean((String) oidc);
	}

	@Override
	protected HttpRequest request(FilterContext<ApiInvocation> context) {
		return context.object().request();
	}

}
