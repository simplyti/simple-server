package com.simplyti.service.security.oidc;

import java.security.Key;

import javax.inject.Singleton;

import com.dslplatform.json.Configuration;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.server.http.api.ApiProvider;
import com.simplyti.server.http.api.filter.OperationInboundFilter;
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
import com.simplyti.service.security.oidc.handler.FullOpenidHandlerConfig;
import com.simplyti.service.security.oidc.handler.OpenIdHandler;
import com.simplyti.service.security.oidc.handler.RedirectableOpenIdHandler;
import com.simplyti.service.security.oidc.jwk.JsonWebKeyConfiguration;

public class OpenIdModule extends AbstractModule {
	
	private final OpenIdHandler openidProvider;
	private final RedirectableOpenIdHandler redirectableOpenidProvider;
	private final FullOpenidHandlerConfig fullOpenidProviderConfig;
	private final FullAutodiscoveredOpenIdConfig fullAutodiscoveredOpenIdConfig;
	private final AutodiscoveredOpenIdConfig autodiscoveredOpenIdConfig;
	
	public OpenIdModule(Key key, String authorizationEndpoint, String tokenEndpoint, String callbackUri, String clientId, String clientSecret,String cipherKey) {
		this.openidProvider=null;
		this.redirectableOpenidProvider=null;
		this.fullOpenidProviderConfig=new FullOpenidHandlerConfig(key,authorizationEndpoint,tokenEndpoint,callbackUri,clientId,clientSecret,cipherKey);
		this.fullAutodiscoveredOpenIdConfig=null;
		this.autodiscoveredOpenIdConfig=null;
	}
	
	public OpenIdModule(Key key, String authorizationEndpoint, String callbackUri, String clientId) {
		this.openidProvider=null;
		this.redirectableOpenidProvider=new DefaultRedirectableOpenIdHandler(key,authorizationEndpoint,callbackUri,clientId);
		this.fullOpenidProviderConfig=null;
		this.fullAutodiscoveredOpenIdConfig=null;
		this.autodiscoveredOpenIdConfig=null;
	}
	
	public OpenIdModule(Key key) {
		this.openidProvider=new DefaultOpenIdHandler(key);
		this.redirectableOpenidProvider=null;
		this.fullOpenidProviderConfig=null;
		this.fullAutodiscoveredOpenIdConfig=null;
		this.autodiscoveredOpenIdConfig=null;
	}
	
	public OpenIdModule(String openIdProvider,String callbackUri, String clientId, String clientSecret,String cipherKey) {
		this.openidProvider=null;
		this.redirectableOpenidProvider=null;
		this.fullOpenidProviderConfig=null;
		this.fullAutodiscoveredOpenIdConfig=new DefaultFullAutodiscoveredOpenIdConfig(openIdProvider,callbackUri,clientId,clientSecret,cipherKey);
		this.autodiscoveredOpenIdConfig=null;
	}

	public OpenIdModule(String callbackUri, String cipherKey) {
		this.openidProvider=null;
		this.redirectableOpenidProvider=null;
		this.fullOpenidProviderConfig=null;
		this.fullAutodiscoveredOpenIdConfig=null;
		this.autodiscoveredOpenIdConfig= new DefaultAutodiscoveredOpenIdConfig(callbackUri,cipherKey);
	}

	@Override
	public void configure() {
		Multibinder.newSetBinder(binder(), Configuration.class).addBinding().to(JsonWebKeyConfiguration.class).in(Singleton.class);
		
		if(openidProvider!=null) {
			Multibinder.newSetBinder(binder(), OperationInboundFilter.class).addBinding().to(OpenIdOperationFilter.class).in(Singleton.class);
			bind(OpenIdHandler.class).toInstance(openidProvider);
		}else if(redirectableOpenidProvider!=null) {
			Multibinder.newSetBinder(binder(), OperationInboundFilter.class).addBinding().to(OpenIdOperationFilter.class).in(Singleton.class);
			bind(OpenIdHandler.class).toInstance(redirectableOpenidProvider);
		}else if(fullOpenidProviderConfig!=null){
			Multibinder.newSetBinder(binder(), OperationInboundFilter.class).addBinding().to(OpenIdOperationFilter.class).in(Singleton.class);
			bind(FullOpenidHandlerConfig.class).toInstance(fullOpenidProviderConfig);
			bind(OpenIdHandler.class).to(DefaultFullOpenidHandler.class);
			bind(HttpClient.class).toProvider(HttpClientProvider.class).in(Singleton.class);
			bind(OpenIdCallbackConfig.class).toInstance(new OpenIdCallbackConfig(fullOpenidProviderConfig.callbackUri(),fullOpenidProviderConfig.cipherKey()));
			Multibinder.newSetBinder(binder(), ApiProvider.class).addBinding().to(OpenIdApi.class).in(Singleton.class);
		}else if (fullAutodiscoveredOpenIdConfig!=null){
			Multibinder.newSetBinder(binder(), OperationInboundFilter.class).addBinding().to(OpenIdOperationFilter.class).in(Singleton.class);
			bind(OpenIdHandler.class).to(AutodiscoveredOpenIdHandler.class);
			bind(HttpClient.class).toProvider(HttpClientProvider.class).in(Singleton.class);
			bind(OpenIdCallbackConfig.class).toInstance(new OpenIdCallbackConfig(fullAutodiscoveredOpenIdConfig.callbackUri(),fullAutodiscoveredOpenIdConfig.cipherKey()));
			Multibinder.newSetBinder(binder(), ApiProvider.class).addBinding().to(OpenIdApi.class).in(Singleton.class);
			bind(FullAutodiscoveredOpenIdConfig.class).toInstance(fullAutodiscoveredOpenIdConfig);
		}else {
			bind(HttpClient.class).toProvider(HttpClientProvider.class).in(Singleton.class);
			bind(OpenIdCallbackConfig.class).toInstance(new OpenIdCallbackConfig(autodiscoveredOpenIdConfig.callbackUri(),autodiscoveredOpenIdConfig.cipherKey()));
			Multibinder.newSetBinder(binder(), ApiProvider.class).addBinding().to(OpenIdApi.class).in(Singleton.class);
			bind(AutodiscoveredOpenIdConfig.class).toInstance(autodiscoveredOpenIdConfig);
		}
	}

}
