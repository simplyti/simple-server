package com.simplyti.service.security.oidc.filter;

import java.util.Map;

import javax.inject.Inject;

import com.simplyti.service.api.filter.FilterContext;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.exception.ForbiddenException;
import com.simplyti.service.exception.UnauthorizedException;
import com.simplyti.service.security.oidc.key.resolver.SigningKeyResolverException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.security.SignatureException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

public class OpenIdHeaderBasedRequestFilter implements HttpRequestFilter {
	
	private static final String BEARER_PREFIX = "Bearer";
	private static final TypeLiteral<Map<String, ?>> CLAIMS = new TypeLiteral<Map<String,?>>(){};
	
	private final Json json;
	private final SigningKeyResolver keyResolver;
	
	@Inject
	public OpenIdHeaderBasedRequestFilter(Json json, SigningKeyResolver keyResolver) {
		this.json=json;
		this.keyResolver=keyResolver;
	}

	@Override
	public void execute(FilterContext<HttpRequest> context) {
		HttpRequest request = context.object();
		if(request.headers().contains(HttpHeaderNames.AUTHORIZATION)) {
			String auth = request.headers().get(HttpHeaderNames.AUTHORIZATION);
			if(auth.startsWith(BEARER_PREFIX)) {
				checkToken(context,request,auth.substring(BEARER_PREFIX.length()));
			}else {
				context.fail(new UnauthorizedException());
			}
		} else {
			context.fail(new UnauthorizedException());
		}
	}
	
	private void checkToken(FilterContext<?> context, HttpRequest request, String token) {
		try{
			Jwts.parser().setSigningKeyResolver(keyResolver)
				.deserializeJsonWith(data->json.deserialize(data, CLAIMS))
				.parseClaimsJws(token);
			request.headers().set(HttpHeaderNames.AUTHORIZATION,BEARER_PREFIX+" "+token);
			context.done();
		} catch (SignatureException| MalformedJwtException e) {
			context.fail(new ForbiddenException());
		}catch (ExpiredJwtException e) {
			context.fail(new ForbiddenException());
		} catch (SigningKeyResolverException e) {
			context.fail(e);
		}
	}

}
