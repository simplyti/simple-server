package com.simplyti.service.security.oidc.filter;

import java.util.Optional;
import java.util.Set;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ServiceUnavailableException;

import com.simplyti.service.api.filter.Filter;
import com.simplyti.service.api.filter.FilterContext;
import com.simplyti.service.security.oidc.callback.OpenIdApi;
import com.simplyti.service.security.oidc.config.auto.AutodiscoveryOpenIdIncompleteException;
import com.simplyti.service.security.oidc.handler.OpenIdHandler;
import com.simplyti.service.security.oidc.handler.RedirectableOpenIdHandler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

public abstract class AbstractOpenIdFilter<T> implements Filter<T>{
	
	private static final String BEARER_PREFIX = "Bearer";
	
	private final OpenIdHandler oidcConfig;
	
	public AbstractOpenIdFilter(OpenIdHandler oidcConfig) {
		this.oidcConfig=oidcConfig;
	}
	
	@Override
	public void execute(FilterContext<T> context) {
		HttpRequest request = request(context);
		if(!request.headers().contains(HttpHeaderNames.AUTHORIZATION)) {
			if(oidcConfig.isFullOpenId()) {
				Optional<Cookie> jwtCookie =  request.headers().getAll(HttpHeaderNames.COOKIE).stream()
						.map(ServerCookieDecoder.LAX::decode)
						.flatMap(Set::stream)
						.filter(cookie->cookie.name().equals(OpenIdApi.JWT_SESSION_COOKIE))
						.findFirst();
				if(jwtCookie.isPresent()) {
					checkToken(context,request,jwtCookie.get().value());
				}else {
					handleRedirect(context,request);
				}
			}else if(oidcConfig.isRedirectable()) {
				handleRedirect(context,request);
			}else {
				context.fail(new NotAuthorizedException(BEARER_PREFIX));
			}
		} else {
			String auth = request.headers().get(HttpHeaderNames.AUTHORIZATION);
			if(auth.startsWith(BEARER_PREFIX)) {
				checkToken(context,request,auth.substring(BEARER_PREFIX.length()));
			}else {
				context.fail(new NotAuthorizedException(BEARER_PREFIX));
			}
		}
	}
	
	protected abstract HttpRequest request(FilterContext<T> context);

	private void checkToken(FilterContext<?> context, HttpRequest request, String token) {
		try{
			Jwts.parser().setSigningKeyResolver(oidcConfig).parseClaimsJws(token);
			request.headers().set(HttpHeaderNames.AUTHORIZATION,BEARER_PREFIX+" "+token);
			context.done();
		} catch (SignatureException| MalformedJwtException e) {
			context.fail(new ForbiddenException());
		}catch (ExpiredJwtException e) {
			handleRedirect(context,request);
		} catch (AutodiscoveryOpenIdIncompleteException e) {
			context.fail(new ServiceUnavailableException());
		}
	}

	private void handleRedirect(FilterContext<?> context, HttpRequest request) {
		RedirectableOpenIdHandler redirectable = oidcConfig.redirectable();
		String authRedirect = redirectable.getAuthorizationUrl(request);
		if(authRedirect==null) {
			context.fail(new NotAuthorizedException(BEARER_PREFIX));
		} else {
			FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
			resp.headers().set(HttpHeaderNames.LOCATION,authRedirect);
			resp.headers().set(HttpHeaderNames.CONTENT_LENGTH,0);
			context.channel().writeAndFlush(resp).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
			context.done(true);
		}
	}

}
