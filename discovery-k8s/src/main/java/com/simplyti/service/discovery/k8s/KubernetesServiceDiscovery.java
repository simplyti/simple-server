package com.simplyti.service.discovery.k8s;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.Address;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.clients.k8s.KubeClient;
import com.simplyti.service.clients.k8s.common.K8sApi;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.Observable;
import com.simplyti.service.clients.k8s.common.watch.domain.EventType;
import com.simplyti.service.clients.k8s.endpoints.domain.Endpoint;
import com.simplyti.service.clients.k8s.endpoints.domain.Port;
import com.simplyti.service.clients.k8s.endpoints.domain.Subset;
import com.simplyti.service.clients.k8s.ingresses.domain.Ingress;
import com.simplyti.service.clients.k8s.ingresses.domain.IngressBackend;
import com.simplyti.service.clients.k8s.ingresses.domain.IngressPath;
import com.simplyti.service.clients.k8s.ingresses.domain.IngressTls;
import com.simplyti.service.clients.k8s.secrets.domain.Secret;
import com.simplyti.service.clients.k8s.services.domain.Service;
import com.simplyti.service.clients.k8s.services.domain.ServicePort;
import com.simplyti.service.discovery.k8s.oidc.K8sAutodiscoveredOpenIdHandler;
import com.simplyti.service.discovery.k8s.oidc.K8sAutodiscoveredOpenIdProviderConfig;
import com.simplyti.service.discovery.k8s.ssl.KubernetesCertificateProvider;
import com.simplyti.service.gateway.DefaultServiceDiscovery;
import com.simplyti.service.gateway.BackendService;
import com.simplyti.service.gateway.ServiceKey;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;
import com.simplyti.service.security.oidc.config.DefaultOpenIdClientConfig;
import com.simplyti.service.security.oidc.config.OpenIdClientConfig;
import com.simplyti.service.security.oidc.config.auto.AutodiscoveredOpenIdConfig;
import com.simplyti.service.security.oidc.filter.OpenIdRequestFilter;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;

public class KubernetesServiceDiscovery extends DefaultServiceDiscovery implements ServerStartHook, ServerStopHook {
	
	private static final String SECURE_BACKENDS_ANN = "ingress.kubernetes.io/secure-backends";
	private static final String AUTH_TYPE = "ingress.kubernetes.io/auth-type";
	private static final String AUTH_REALM = "ingress.kubernetes.io/auth-realm";
	private static final String AUTH_SECRET = "ingress.kubernetes.io/auth-secret";
	private static final String REWRITE_ANN = "ingress.kubernetes.io/rewrite-target";

	private final EventLoop eventLoop;
	
	private final KubeClient client;
	
	private final Map<String,Service> services = new HashMap<>();
	private Observable<Service> observableServices;
	
	private final Map<String,Ingress> ingresses = new HashMap<>();
	private Observable<Ingress> observableIngresses;
	
	private final Map<String,Endpoint> endpoints = new HashMap<>();
	private Observable<Endpoint> observableEndpoints;
	
	private Observable<Secret> observableSecrets;

	private final KubernetesCertificateProvider certificateProvider;
	private final HttpClient http;
	
	private final AutodiscoveredOpenIdConfig openIdConfig;
	private final Map<String,OpenIdClientConfig> openIdClientSecrets;
	private final Json json;
	
	@Inject
	public KubernetesServiceDiscovery(EventLoopGroup eventLoopgroup,KubeClient client, HttpClient http, KubernetesCertificateProvider certificateProvider,
			AutodiscoveredOpenIdConfig openIdConfig, Json json) {
		this.client=client;
		this.http=http;
		this.eventLoop=eventLoopgroup.next();
		this.certificateProvider=certificateProvider;
		this.openIdConfig=openIdConfig;
		this.openIdClientSecrets=new ConcurrentHashMap<>();
		this.json=json;
	}
	
	@Override
	public Future<Void> executeStart(EventLoop executor) {
		Promise<Void> listPromise = executor.newPromise();
		PromiseCombiner combiner = new PromiseCombiner(executor);
		AtomicReference<String> servicesResourceVersion = new AtomicReference<>();
		AtomicReference<String> ingressesResourceVersion = new AtomicReference<>();
		AtomicReference<String> endpointsResourceVersion = new AtomicReference<>();
		AtomicReference<String> secretsResourceVersion = new AtomicReference<>();
		
		combiner.add(list(executor, client.services(), servicesResourceVersion, this::handleService));
		combiner.add(list(executor, client.ingresses(), ingressesResourceVersion, this::handleIngress));
		combiner.add(list(executor, client.endpoints(), endpointsResourceVersion, this::handleEndpoint));
		combiner.add(list(executor, client.secrets(), secretsResourceVersion, this::handleSecret));
		combiner.finish(listPromise);
		Promise<Void> promise = executor.newPromise();
		listPromise.addListener(f->{
			if(f.isSuccess()) {
				observableServices = watch(client.services(),servicesResourceVersion.get(),this::handleService);
				observableIngresses = watch(client.ingresses(),ingressesResourceVersion.get(),this::handleIngress);
				observableEndpoints = watch(client.endpoints(),endpointsResourceVersion.get(),this::handleEndpoint);
				observableSecrets = watch(client.secrets(),secretsResourceVersion.get(),this::handleSecret);
				promise.setSuccess(null);
			}else {
				promise.setFailure(f.cause());
			}
		});
		return promise;
	}
	
	@Override
	public Future<Void> executeStop(EventLoop executor) {
		Promise<Void> promise = executor.newPromise();
		PromiseCombiner combiner = new PromiseCombiner(executor);
		combiner.add(stopObserve(observableServices, executor));
		combiner.add(stopObserve(observableIngresses, executor));
		combiner.add(stopObserve(observableEndpoints, executor));
		combiner.add(stopObserve(observableSecrets, executor));
		combiner.finish(promise);
		return promise;
	}
	
	private Future<Void> stopObserve(Observable<?> observable, EventLoop executor) {
		if(observable==null) {
			return executor.newSucceededFuture(null);
		}
		Promise<Void> promise = executor.newPromise();
		observable.close().addListener(f->promise.setSuccess(null));
		return promise;
	}

	private <T extends K8sResource> Future<Void> list(EventLoop executor, K8sApi<T> api,AtomicReference<String> resourceVersion, EventConsumer<T> consumer) {
		Promise<Void> promise = executor.newPromise();
		Future<KubeList<T>> future = api.list();
		future.addListener(f->{
			if(f.isSuccess()) {
				if(executor.inEventLoop()) {
					handleList(executor,promise,resourceVersion,future.getNow(),consumer);
				}else {
					executor.submit(()->handleList(executor,promise,resourceVersion,future.getNow(),consumer));
				}
			}else {
				promise.setFailure(f.cause());
			}
		});
		return promise;
	}
	
	private <T extends K8sResource> void handleList(EventLoop executor, Promise<Void> promise, AtomicReference<String> resourceVersion, KubeList<T> list, EventConsumer<T> consumer) {
		PromiseCombiner combiner = new PromiseCombiner(executor);
		list.items().forEach(service->combiner.add(handle(executor, EventType.ADDED, service,consumer)));
		resourceVersion.set(list.metadata().resourceVersion());
		combiner.finish(promise);
	}

	private <T extends K8sResource> Observable<T> watch(K8sApi<T> api, String resourceVersion, EventConsumer<T> consumer) {
		return api.watch(resourceVersion).on(event->handle(eventLoop, event.type(),event.object(),consumer));
	}
	
	private <T extends K8sResource> Future<Void> handle(EventLoop executor, EventType type, T service, EventConsumer<T> consumer) {
		if(type == EventType.ERROR) {
			return executor.newSucceededFuture(null);
		}
		Promise<Void> promise = executor.newPromise();
		if(executor.inEventLoop()) {
			consumer.accept(type,service,promise);
		}else {
			executor.execute(()->consumer.accept(type,service,promise));
		}
		return promise;
	}
	
	private Future<Void> handleService(EventType type, Service service, Promise<Void> promise) {
		String id = resourceId(service.metadata());
		if(type == EventType.ADDED){
			services.put(id, service);
			if(endpoints.containsKey(id)) {
				address(endpoints.get(id)).forEach(address->eachEndpoint(service, address, (edp,host,path)->addEndpoint(host, null, path, edp)));
			}
		}else if(type == EventType.DELETED) {
			Service deletedService = services.remove(id);
			ingresses.values().stream()
				.filter(ingress->sameNamespace(ingress,deletedService))
				.forEach(ingress->ingress.spec().rules().stream()
					.forEach(rule->rule.http().paths().stream()
						.filter(path->isBackend(path, deletedService))
						.forEach(path->clear(rule.host(),null,path.path()))));
		}else if(type == EventType.MODIFIED) {
			Service oldService = services.put(id, service);
			if(endpoints.containsKey(id)) {
				Endpoint endpoint = endpoints.get(id);
				List<EnpointAddress> addresses = address(endpoint);
				Map<ServiceKey, List<com.simplyti.service.clients.Endpoint>> oldEndpoints = endpoints(oldService,addresses);
				Map<ServiceKey, List<com.simplyti.service.clients.Endpoint>> newEndpoints = endpoints(service,addresses);
				oldEndpoints.forEach((key,edps)->edps.stream().filter(edp->!newEndpoints.containsKey(key) || !newEndpoints.get(key).contains(edp))
						.forEach(edp->deleteEndpoint(key.host(), key.method(), key.path(), edp)));
				newEndpoints.forEach((key,edps)->edps.stream().filter(edp->!oldEndpoints.containsKey(key) || !oldEndpoints.get(key).contains(edp))
						.forEach(edp->addEndpoint(key.host(), key.method(), key.path(), edp)));
			}
		}
		return promise.setSuccess(null);
	}
	
	private Map<ServiceKey,List<com.simplyti.service.clients.Endpoint>> endpoints(Service service, List<EnpointAddress> addresses) {
		Map<ServiceKey,List<com.simplyti.service.clients.Endpoint>> mapedServices = new HashMap<>();
		addresses.forEach(address->eachEndpoint(service, address, (edp,host,path)->{
						ServiceKey key = new ServiceKey(host,null,path);
						if(mapedServices.containsKey(key)) {
							mapedServices.get(key).add(edp);
						}else {
							List<com.simplyti.service.clients.Endpoint> edps = new ArrayList<>();
							edps.add(edp);
							mapedServices.put(key, edps);
						}
					}));
		return mapedServices;
	}

	private Future<Void> handleIngress(EventType type, Ingress ingress, Promise<Void> promise) {
		String id = resourceId(ingress.metadata());
		boolean tlsEnabled = ingress.spec().tls()!=null;
		if(type == EventType.ADDED){
			ingresses.put(id, ingress);
			MoreObjects.firstNonNull(ingress.spec().tls(), Collections.<IngressTls>emptyList()).stream()
				.forEach(tls->tls.hosts().stream().forEach(host->certificateProvider
						.add(host,Joiner.on(':').join(ingress.metadata().namespace(),tls.secretName()))));
			
			ingress.spec().rules().stream().flatMap(rule->rule.http().paths().stream()
					.map(path->new BackendService(rule.host(), null, path.path(),rewrite(ingress),tlsEnabled,securiFilters(ingress),
							endpoints(ingress.metadata().namespace(),ingress,path.backend()))))
			.forEach(this::addService);
		}else if(type == EventType.DELETED) {
			Ingress oldIngress = ingresses.remove(id);
			
			MoreObjects.firstNonNull(oldIngress.spec().tls(), Collections.<IngressTls>emptyList()).stream()
				.forEach(tls->tls.hosts().stream().forEach(certificateProvider::remove));
			
			oldIngress.spec().rules().stream().flatMap(rule->rule.http().paths().stream()
					.map(path->new BackendService(rule.host(), null, path.path(),null,tlsEnabled, null,null)))
			.forEach(this::removeService);
		} else if(type == EventType.MODIFIED) {
			Ingress oldIngress = ingresses.put(id, ingress);
			
			MoreObjects.firstNonNull(ingress.spec().tls(), Collections.<IngressTls>emptyList()).stream()
			.forEach(tls->tls.hosts().stream().forEach(host->certificateProvider
					.add(host,Joiner.on(':').join(ingress.metadata().namespace(),tls.secretName()))));
			
			MoreObjects.firstNonNull(oldIngress.spec().tls(), Collections.<IngressTls>emptyList()).stream()
			.forEach(tls->tls.hosts().stream().forEach(certificateProvider::remove));
			
			List<BackendService> oldBackends = oldIngress.spec().rules().stream().flatMap(rule->rule.http().paths().stream()
					.map(path->new BackendService(rule.host(), null, path.path(),null, tlsEnabled, null, endpoints(ingress.metadata().namespace(),ingress,path.backend()))))
					.collect(Collectors.toList());
			List<BackendService> newBackends = ingress.spec().rules().stream().flatMap(rule->rule.http().paths().stream()
					.map(path->new BackendService(rule.host(), null, path.path(),rewrite(ingress),tlsEnabled,  securiFilters(ingress), 
							endpoints(ingress.metadata().namespace(),ingress,path.backend()))))
					.collect(Collectors.toList());
			
			oldBackends.stream().filter(backend->!newBackends.contains(backend))
				.forEach(this::removeService);
			newBackends.stream().filter(backend->!oldBackends.contains(backend))
				.forEach(this::addService);
		}
		return promise.setSuccess(null);
	}
	
	private String rewrite(Ingress ingress) {
		if(containsAnnotation(ingress,REWRITE_ANN)) {
			return ingress.metadata().annotations().get(REWRITE_ANN);
		} else {
			return null;
		}
	}

	private Set<HttpRequestFilter> securiFilters(Ingress ingress) {
		if(annIsEquals(ingress,AUTH_TYPE,"oidc") && containsAnnotation(ingress,AUTH_REALM) && containsAnnotation(ingress,AUTH_SECRET)) {
			return Collections.singleton(new OpenIdRequestFilter(json,new K8sAutodiscoveredOpenIdHandler(http,
					openIdClientSecrets,ingress.metadata().namespace(),
					ingress.metadata().annotations().get(AUTH_SECRET),
						new K8sAutodiscoveredOpenIdProviderConfig(ingress.metadata().annotations().get(AUTH_REALM),openIdConfig.callbackUri(),openIdConfig.cipherKey()),json)));
		} else {
			return null;
		}
			
	}

	private Future<Void> handleSecret(EventType type, Secret secret, Promise<Void> promise) {
		if(secret.data()==null) {
			return promise.setSuccess(null);
		}
		if(secret.data().containsKey("tls.key") && secret.data().containsKey("tls.crt")) {
			if(type == EventType.ADDED){
				certificateProvider.addSecret(resourceId(secret.metadata()),secret);
			}else if(type == EventType.DELETED) {
				certificateProvider.removeSecret(resourceId(secret.metadata()));
			}else if(type == EventType.MODIFIED) {
				certificateProvider.updateSecret(resourceId(secret.metadata()),secret);
			}
		}else if(secret.data().containsKey("clientId") && secret.data().containsKey("clientSecret")){
			if(type == EventType.ADDED || type == EventType.MODIFIED){
				openIdClientSecrets.put(resourceId(secret.metadata()), 
						new DefaultOpenIdClientConfig(secret.data().get("clientId").asString(CharsetUtil.UTF_8),
								secret.data().get("clientSecret").asString(CharsetUtil.UTF_8)));
			}else if(type == EventType.DELETED) {
				openIdClientSecrets.remove(resourceId(secret.metadata()));
			}
		}
		return promise.setSuccess(null);
	}
	
	private Future<Void> handleEndpoint(EventType type, Endpoint endpoint, Promise<Void> promise) {
		String id = resourceId(endpoint.metadata());
		if(type == EventType.ADDED){
			endpoints.put(id, endpoint);
			if(services.containsKey(id)) {
				 address(endpoints.get(id)).forEach(newAddress->eachEndpoint(id,newAddress,(edp,host,path)->addEndpoint(host,null,path,edp)));
			}
		}else if(type == EventType.DELETED) {
			address(endpoint).forEach(address->eachEndpoint(id,address,(edp,host,path)->deleteEndpoint(host,null,path,edp)));
		}else if(type == EventType.MODIFIED) {
			List<EnpointAddress> oldAddresses = address(endpoints.put(id, endpoint));
			List<EnpointAddress> newAddresses = address(endpoint);
			oldAddresses.stream()
				.filter(address->!newAddresses.contains(address))
				.forEach(deletedAddress->eachEndpoint(id,deletedAddress,(edp,host,path)->deleteEndpoint(host,null,path,edp)));
			newAddresses.stream().filter(address->!oldAddresses.contains(address))
				.forEach(newAddress->eachEndpoint(id,newAddress,(edp,host,path)->addEndpoint(host,null,path,edp)));
		}
		return promise.setSuccess(null);
	}

	private List<EnpointAddress> address(Endpoint oldEndpoint) {
		return MoreObjects.firstNonNull(oldEndpoint.subsets(), Collections.<Subset>emptyList()).stream()
		.flatMap(subset->subset.ports().stream()
				.flatMap(port->MoreObjects.firstNonNull(subset.addresses(), Collections.<com.simplyti.service.clients.k8s.endpoints.domain.Address>emptyList()).stream()
						.map(address->new EnpointAddress(port.name(),new Address(address.ip(), port.port())))))
		.collect(Collectors.toList());
	}
	
	private void eachEndpoint(String id, EnpointAddress address,EndpointConsumer consumer) {
		if(services.containsKey(id)) {
			Service service = services.get(id);
			eachEndpoint(service,address,consumer);
		}
	}

	private void eachEndpoint(Service service, EnpointAddress address, EndpointConsumer consumer) {
		ingresses.values().stream()
		.filter(ingress->sameNamespace(ingress,service))
		.forEach(ingress->ingress.spec().rules()
			.forEach(rule->rule.http().paths().stream()
				.filter(path->isBackend(path, service))
				.filter(path->isTarget(service,address))
				.forEach(path->consumer.consume(endpoint(ingress,address.address()), rule.host(),path.path()))));
	}

	private boolean isTarget(Service service, EnpointAddress address) {
		if(service.spec().ports().size()==1) {
			return true;
		}
		return service.spec().ports().stream().anyMatch(port->{
			if(port.targetPort() instanceof String) {
				return port.targetPort().equals(address.portName());
			}else {
				return port.targetPort().equals(address.address().port());
			}
		});
	}

	private boolean isBackend(IngressPath path, Service service) {
		return path.backend().serviceName().equals(service.metadata().name())
				&& service.spec().ports().stream().anyMatch(port->isTargetPort(port,path.backend().servicePort()));
	}

	private boolean sameNamespace(K8sResource a, K8sResource b) {
		return a.metadata().namespace().equals(b.metadata().namespace());
	}

	private Collection<com.simplyti.service.clients.Endpoint> endpoints(String namespace, Ingress ingress, IngressBackend backend) {
		String serviceId = Joiner.on(':').join(namespace,backend.serviceName());
		if(services.containsKey(serviceId) && endpoints.containsKey(serviceId)) {
			return services.get(serviceId).spec().ports().stream()
					.filter(port->isTargetPort(port,backend.servicePort()))
					.flatMap(servicePort-> MoreObjects.firstNonNull(endpoints.get(serviceId).subsets(), Collections.<Subset>emptyList())
					.stream().flatMap(subset->subset.ports().stream().filter(port->port.port().equals(servicePort.targetPort()))
						.flatMap(port->subset.addresses().stream()
								.map(address->endpoint(ingress,address,port))))
			).collect(Collectors.toList());
		}else {
			return Collections.emptyList();
		}
	}

	private boolean isTargetPort(ServicePort port, Object servicePort) {
		if(servicePort instanceof String) {
			return port.name().equals((String)servicePort);
		}else {
			return port.port().equals(servicePort);
		}
	}

	private com.simplyti.service.clients.Endpoint endpoint(Ingress ingress, com.simplyti.service.clients.k8s.endpoints.domain.Address address, Port port) {
		return endpoint(ingress,new Address(address.ip(), port.port()));
	}
	
	private com.simplyti.service.clients.Endpoint endpoint(Ingress ingress, Address address) {
		return new com.simplyti.service.clients.Endpoint(isSsl(ingress)?HttpEndpoint.HTTPS_SCHEMA:HttpEndpoint.HTTP_SCHEMA,address);
	}

	private boolean isSsl(Ingress ingress) {
		return isAnnTrue(ingress,SECURE_BACKENDS_ANN);
	}
	
	private boolean isAnnTrue(K8sResource resource, String ann) {
		return containsAnnotation(resource,ann) && 
				Boolean.parseBoolean(resource.metadata().annotations().get(ann));
	}

	private boolean containsAnnotation(K8sResource resource, String ann) {
		return resource.metadata().annotations()!=null &&
				resource.metadata().annotations().containsKey(ann);
	}
	
	private boolean annIsEquals(K8sResource resource, String ann, String value) {
		return containsAnnotation(resource,ann) && 
				value.equals(resource.metadata().annotations().get(ann));
	}

	private String resourceId(Metadata metadata) {
		return Joiner.on(':').join(metadata.namespace(), metadata.name());
	}

}
