package com.simplyti.service.security.oidc.callback;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;

import com.google.common.base.Joiner;
import com.simplyti.server.http.api.ApiProvider;
import com.simplyti.server.http.api.builder.ApiBuilder;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.security.oidc.OpenIdCallbackConfig;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor=@__(@Inject))
public class OpenIdApi implements ApiProvider{
	
	public static final String JWT_SESSION_COOKIE = "JWT-SESSION";
	
	private final OpenIdCallbackConfig config;
	
	private final HttpClient httpClient;
	
	private final Json json;

	@Override
	public void build(ApiBuilder builder) {
		builder.when().get(config.callbackUri())
			.then(ctx->{
				State state;
				try {
					String state64 = ctx.queryParam("state").replaceAll(" ", "+");
					Cipher ci = Cipher.getInstance("AES");
					ci.init(Cipher.DECRYPT_MODE, config.cipherKey());
					byte[] decrypted = ci.doFinal(Base64.getDecoder().decode(state64));
					state =  json.deserialize(decrypted,State.class);
				} catch (IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException e) {
					throw new IllegalStateException(e);
				}
				
				String data = Joiner.on('&').join(
						Joiner.on('=').join("grant_type", "authorization_code"),
						Joiner.on('=').join("code", ctx.queryParam("code")),
						Joiner.on('=').join("client_id", state.clientId()),
						Joiner.on('=').join("client_secret", state.clientSecret()),
						Joiner.on('=').join("redirect_uri", state.redirectUri()));
				
				HttpEndpoint tokenEndpoint = HttpEndpoint.of(state.tokenEndpoint().toString());
				FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, tokenEndpoint.path(),Unpooled.wrappedBuffer(data.getBytes(CharsetUtil.UTF_8)));
				request.headers().set(HttpHeaderNames.HOST,tokenEndpoint.address().host());
				request.headers().set(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED);
				request.headers().set(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
				
				Future<FullHttpResponse> result = httpClient.request().withEndpoint(tokenEndpoint)
					.withCheckStatusCode()
					.send(request)
					.fullResponse();
				
				result.addListener(f->{
						if(f.isSuccess()) {
							Token token = json.deserialize(result.getNow().content(),Token.class);
							result.getNow().release();

							FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
							resp.headers().set(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.LAX.encode(
									cookie(token.id_token(),token.expires_in())));
							resp.headers().set(HttpHeaderNames.LOCATION,state.redirectUri());
							resp.headers().set(HttpHeaderNames.CONTENT_LENGTH,0);
							ctx.send(resp);
						}else {
							ctx.failure(f.cause());
						}
					});
			});
		
	}
	
	private Cookie cookie(String token, int expiresIn) {
		Cookie sessionCookie = new DefaultCookie(JWT_SESSION_COOKIE, token);
		sessionCookie.setHttpOnly(false);
		sessionCookie.setSecure(true);
		sessionCookie.setPath("/");
		sessionCookie.setMaxAge(expiresIn);
		return sessionCookie;
	}

}
