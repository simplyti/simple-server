package com.simplyti.service.auth;

import javax.inject.Inject;

import com.simplyti.service.api.filter.FilterContext;
import com.simplyti.service.api.filter.OperationInboundFilter;
import com.simplyti.service.exception.UnauthorizedException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class JWTAuthFilter implements OperationInboundFilter {
	
	private static final String BEARER_PREFIX = "Bearer";
	
	private final JWTConfiguration jwtConfiguration;

	@Override
	public void execute(FilterContext context) {
		if(!context.operation().requiresAuth()) {
			context.done();
		}else if(!context.headers().contains(HttpHeaderNames.AUTHORIZATION)) {
			context.fail(new UnauthorizedException());
		} else {
			String auth = context.headers().get(HttpHeaderNames.AUTHORIZATION);
			if(auth.startsWith(BEARER_PREFIX)) {
				String token = auth.substring(BEARER_PREFIX.length());
				try{
					Jwts.parser().setSigningKey(jwtConfiguration.key()).parseClaimsJws(token);
					context.done();
				} catch (SignatureException| MalformedJwtException e) {
					context.fail(new UnauthorizedException());
				}
			}else {
				context.fail(new UnauthorizedException());
			}
		}
	}

}
