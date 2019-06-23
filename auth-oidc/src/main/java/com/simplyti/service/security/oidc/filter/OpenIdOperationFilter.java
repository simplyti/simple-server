package com.simplyti.service.security.oidc.filter;

import javax.inject.Inject;

import com.simplyti.service.api.ApiInvocation;
import com.simplyti.service.api.filter.FilterContext;
import com.simplyti.service.api.filter.OperationInboundFilter;
import com.simplyti.service.security.oidc.OpenIdModule;
import com.simplyti.service.security.oidc.handler.OpenIdHandler;

import io.netty.handler.codec.http.HttpRequest;

public class OpenIdOperationFilter extends AbstractOpenIdFilter<ApiInvocation> implements OperationInboundFilter{
	
	@Inject
	public OpenIdOperationFilter(OpenIdHandler oidcConfig) {
		super(oidcConfig);
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
		Object oidc = context.object().operation().meta(OpenIdModule.META_ATT);
		return oidc!=null && oidc instanceof String && Boolean.parseBoolean((String) oidc);
	}

	@Override
	protected HttpRequest request(FilterContext<ApiInvocation> context) {
		return context.object().request();
	}

}
