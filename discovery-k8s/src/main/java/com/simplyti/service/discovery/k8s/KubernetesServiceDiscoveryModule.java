package com.simplyti.service.discovery.k8s;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.clients.k8s.KubeClient;
import com.simplyti.service.discovery.k8s.ssl.KubernetesCertificateProvider;
import com.simplyti.service.gateway.ServiceDiscovery;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;
import com.simplyti.service.security.oidc.OpenIdModule;
import com.simplyti.service.ssl.ServerCertificateProvider;

public class KubernetesServiceDiscoveryModule extends AbstractModule{
	
	private final String callbackUri;
	private final String cipherKey;
	private final String apiServer;
	
	public KubernetesServiceDiscoveryModule(String apiServer) {
		this("/_api/callback","myCipherKey",apiServer);
	}
	
	public KubernetesServiceDiscoveryModule() {
		this("/_api/callback","myCipherKey");
	}
	
	public KubernetesServiceDiscoveryModule(String callbackUri,String cipherKey) {
		this(callbackUri,cipherKey,null);
	}

	public KubernetesServiceDiscoveryModule(String callbackUri,String cipherKey,String apiServer) {
		this.callbackUri=callbackUri;
		this.cipherKey=cipherKey;
		this.apiServer=apiServer;
	}
	
	@Override
	public void configure() {
		install(new OpenIdModule(callbackUri,cipherKey));
		
		bind(KubernetesDiscoveryConfig.class).toInstance(new KubernetesDiscoveryConfig(apiServer));
		bind(KubeClient.class).toProvider(KubeClientProvider.class).in(Singleton.class);
		
		bind(KubernetesServiceDiscovery.class).in(Singleton.class);
		bind(ServiceDiscovery.class).to(KubernetesServiceDiscovery.class).in(Singleton.class);
		
		bind(ServerCertificateProvider.class).to(KubernetesCertificateProvider.class).in(Singleton.class);
		bind(KubernetesCertificateProvider.class).in(Singleton.class);
		
		Multibinder.newSetBinder(binder(), ServerStartHook.class).addBinding().to(KubernetesServiceDiscovery.class).in(Singleton.class);
		Multibinder.newSetBinder(binder(), ServerStopHook.class).addBinding().to(KubernetesServiceDiscovery.class).in(Singleton.class);
	}

}
