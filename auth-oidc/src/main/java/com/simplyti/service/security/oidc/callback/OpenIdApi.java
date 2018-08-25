package com.simplyti.service.security.oidc.callback;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.inject.Inject;

import com.google.common.base.Joiner;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
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
import io.vavr.control.Try;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor=@__(@Inject))
public class OpenIdApi implements ApiProvider{
	
	public static final String JWT_SESSION_COOKIE = "JWT-SESSION";
	
	private final OpenIdCallbackConfig config;
	
	private final HttpClient httpClient;

	@Override
	public void build(ApiBuilder builder) {
		builder.when().get(config.callbackUri())
			.then(ctx->{
				String state64 = ctx.queryParam("state").replaceAll(" ", "+");
				Cipher ci = Try.of(()->Cipher.getInstance("AES")).get();
				Try.run(()->ci.init(Cipher.DECRYPT_MODE, config.cipherKey())).get();
				byte[] decrypted = Try.<byte[]>of(()->ci.doFinal(Base64.getDecoder().decode(state64))).get();
				
				Any state =  JsonIterator.deserialize(decrypted);
				
				String data = Joiner.on('&').join(
						Joiner.on('=').join("grant_type", "authorization_code"),
						Joiner.on('=').join("code", ctx.queryParam("code")),
						Joiner.on('=').join("client_id", state.get("clientId")),
						Joiner.on('=').join("client_secret", state.get("clientSecret")),
						Joiner.on('=').join("redirect_uri", state.get("redirectUri")));
				
				HttpEndpoint tokenEndpoint = HttpEndpoint.of(state.get("tokenEndpoint").toString());
				FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, tokenEndpoint.path(),Unpooled.wrappedBuffer(data.getBytes(CharsetUtil.UTF_8)));
				request.headers().set(HttpHeaderNames.HOST,tokenEndpoint.address().host());
				request.headers().set(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED);
				request.headers().set(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
				
				Future<FullHttpResponse> result = httpClient.withEndpoin(tokenEndpoint)
					.withCheckStatusCode()
					.sendFull(request).fullResponse();
				
				result.addListener(f->{
						if(f.isSuccess()) {
							byte[] responseBytes = new byte[result.getNow().content().readableBytes()];
							result.getNow().content().readBytes(responseBytes);
							result.getNow().release();
							Any token = JsonIterator.deserialize(responseBytes);

							FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
							resp.headers().set(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.LAX.encode(
									cookie(token.get("id_token").toString(),token.get("expires_in").toInt())));
							resp.headers().set(HttpHeaderNames.LOCATION,state.get("redirectUri").toString());
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
