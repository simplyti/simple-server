package com.simplyti.service.security.oidc;

import java.security.Key;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.jsoniter.spi.JsoniterSpi;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.api.filter.OperationInboundFilter;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.security.oidc.callback.OpenIdApi;
import com.simplyti.service.security.oidc.config.auto.AutodiscoveredOpenIdConfig;
import com.simplyti.service.security.oidc.config.auto.DefaultAutodiscoveredOpenIdConfig;
import com.simplyti.service.security.oidc.config.auto.DefaultFullAutodiscoveredOpenIdConfig;
import com.simplyti.service.security.oidc.config.auto.FullAutodiscoveredOpenIdConfig;
import com.simplyti.service.security.oidc.filter.OpenIdOperationFilter;
import com.simplyti.service.security.oidc.handler.AutodiscoveredOpenIdHandler;
import com.simplyti.service.security.oidc.handler.DefaultFullOpenidHandler;
import com.simplyti.service.security.oidc.handler.DefaultOpenIdHandler;
import com.simplyti.service.security.oidc.handler.DefaultRedirectableOpenIdHandler;
import com.simplyti.service.security.oidc.handler.FullOpenidHandler;
import com.simplyti.service.security.oidc.handler.OpenIdHandler;
import com.simplyti.service.security.oidc.handler.RedirectableOpenIdHandler;
import com.simplyti.service.security.oidc.jwk.JsonWebKey;
import com.simplyti.service.security.oidc.jwk.JsonWebKeyDecoder;

public class OpenIdModule extends AbstractModule {
	
	public static final String META_ATT = "auth.oidc";
	
	private final OpenIdHandler openidProvider;
	private final RedirectableOpenIdHandler redirectableOpenidProvider;
	private final FullOpenidHandler fullOpenidProvider;
	private final FullAutodiscoveredOpenIdConfig fullAutodiscoveredOpenId;
	private final AutodiscoveredOpenIdConfig autodiscoveredOpenId;
	
	public OpenIdModule(Key key, String authorizationEndpoint, String tokenEndpoint, String callbackUri, String clientId, String clientSecret,String cipherKey) {
		this.openidProvider=null;
		this.redirectableOpenidProvider=null;
		this.fullOpenidProvider=new DefaultFullOpenidHandler(key,authorizationEndpoint,tokenEndpoint,callbackUri,clientId,clientSecret,cipherKey);
		this.fullAutodiscoveredOpenId=null;
		this.autodiscoveredOpenId=null;
	}
	
	public OpenIdModule(Key key, String authorizationEndpoint, String callbackUri, String clientId) {
		this.openidProvider=null;
		this.redirectableOpenidProvider=new DefaultRedirectableOpenIdHandler(key,authorizationEndpoint,callbackUri,clientId);
		this.fullOpenidProvider=null;
		this.fullAutodiscoveredOpenId=null;
		this.autodiscoveredOpenId=null;
	}
	
	public OpenIdModule(Key key) {
		this.openidProvider=new DefaultOpenIdHandler(key);
		this.redirectableOpenidProvider=null;
		this.fullOpenidProvider=null;
		this.fullAutodiscoveredOpenId=null;
		this.autodiscoveredOpenId=null;
	}
	
	public OpenIdModule(String openIdProvider,String callbackUri, String clientId, String clientSecret,String cipherKey) {
		this.openidProvider=null;
		this.redirectableOpenidProvider=null;
		this.fullOpenidProvider=null;
		this.fullAutodiscoveredOpenId=new DefaultFullAutodiscoveredOpenIdConfig(openIdProvider,callbackUri,clientId,clientId,cipherKey);
		this.autodiscoveredOpenId=null;
	}

	public OpenIdModule(String callbackUri, String cipherKey) {
		this.openidProvider=null;
		this.redirectableOpenidProvider=null;
		this.fullOpenidProvider=null;
		this.fullAutodiscoveredOpenId=null;
		this.autodiscoveredOpenId= new DefaultAutodiscoveredOpenIdConfig(callbackUri,cipherKey);
	}

	@Override
	public void configure() {
		JsoniterSpi.registerTypeDecoder(JsonWebKey.class, new JsonWebKeyDecoder());
		
		if(openidProvider!=null) {
			Multibinder.newSetBinder(binder(), OperationInboundFilter.class).addBinding().to(OpenIdOperationFilter.class).in(Singleton.class);
			bind(OpenIdHandler.class).toInstance(openidProvider);
		}else if(redirectableOpenidProvider!=null) {
			Multibinder.newSetBinder(binder(), OperationInboundFilter.class).addBinding().to(OpenIdOperationFilter.class).in(Singleton.class);
			bind(OpenIdHandler.class).toInstance(redirectableOpenidProvider);
		}else if(fullOpenidProvider!=null){
			Multibinder.newSetBinder(binder(), OperationInboundFilter.class).addBinding().to(OpenIdOperationFilter.class).in(Singleton.class);
			bind(OpenIdHandler.class).toInstance(fullOpenidProvider);
			bind(HttpClient.class).toProvider(HttpClientProvider.class).in(Singleton.class);
			bind(OpenIdCallbackConfig.class).toInstance(new OpenIdCallbackConfig(fullOpenidProvider.callbackUri(),fullOpenidProvider.cipherKey()));
			Multibinder.newSetBinder(binder(), ApiProvider.class).addBinding().to(OpenIdApi.class).in(Singleton.class);
		}else if (fullAutodiscoveredOpenId!=null){
			Multibinder.newSetBinder(binder(), OperationInboundFilter.class).addBinding().to(OpenIdOperationFilter.class).in(Singleton.class);
			bind(OpenIdHandler.class).to(AutodiscoveredOpenIdHandler.class);
			bind(HttpClient.class).toProvider(HttpClientProvider.class).in(Singleton.class);
			bind(OpenIdCallbackConfig.class).toInstance(new OpenIdCallbackConfig(fullAutodiscoveredOpenId.callbackUri(),fullAutodiscoveredOpenId.cipherKey()));
			Multibinder.newSetBinder(binder(), ApiProvider.class).addBinding().to(OpenIdApi.class).in(Singleton.class);
			bind(FullAutodiscoveredOpenIdConfig.class).toInstance(fullAutodiscoveredOpenId);
		}else {
			bind(HttpClient.class).toProvider(HttpClientProvider.class).in(Singleton.class);
			bind(OpenIdCallbackConfig.class).toInstance(new OpenIdCallbackConfig(autodiscoveredOpenId.callbackUri(),autodiscoveredOpenId.cipherKey()));
			Multibinder.newSetBinder(binder(), ApiProvider.class).addBinding().to(OpenIdApi.class).in(Singleton.class);
			bind(AutodiscoveredOpenIdConfig.class).toInstance(autodiscoveredOpenId);
		}
	}

}