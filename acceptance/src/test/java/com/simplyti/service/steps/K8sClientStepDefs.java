package com.simplyti.service.steps;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;

import com.google.common.collect.Iterables;
import com.simplyti.service.clients.k8s.KubeClient;
import com.simplyti.service.clients.k8s.common.domain.Status;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.Observable;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.common.watch.domain.EventType;
import com.simplyti.service.clients.k8s.endpoints.domain.Address;
import com.simplyti.service.clients.k8s.endpoints.domain.Endpoint;
import com.simplyti.service.clients.k8s.endpoints.domain.Port;
import com.simplyti.service.clients.k8s.ingresses.builder.IngressBuilder;
import com.simplyti.service.clients.k8s.ingresses.domain.Ingress;
import com.simplyti.service.clients.k8s.ingresses.domain.IngressPath;
import com.simplyti.service.clients.k8s.ingresses.domain.IngressRule;
import com.simplyti.service.clients.k8s.namespaces.domain.Namespace;
import com.simplyti.service.clients.k8s.secrets.builder.SecretBuilder;
import com.simplyti.service.clients.k8s.secrets.domain.Secret;
import com.simplyti.service.clients.k8s.serviceaccounts.domain.ServiceAccount;
import com.simplyti.service.clients.k8s.services.domain.Service;
import com.simplyti.service.clients.k8s.services.domain.ServicePort;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.netty.util.concurrent.Future;
import io.vavr.control.Try;

public class K8sClientStepDefs {
	
	private static final String CREATED_NAMESPACES = "_CREATED_NAMESPACES_";
	
	@Inject
	private KubeClient kubeClient;
	
	@Inject
	private Map<String,Object> scenarioData;
	
	@SuppressWarnings("unchecked")
	@Given("^a namespace \"([^\"]*)\"$")
	public void aNamespace(String name) throws Exception {
		Future<Namespace> future = kubeClient.namespaces().builder()
				.withName(name).build();
		Awaitility.await().until(future::isSuccess);
		if(scenarioData.containsKey(CREATED_NAMESPACES)){
			((List<String>)scenarioData.get(CREATED_NAMESPACES)).add(name);
		}else{
			List<String> namespaces = new ArrayList<>();
			namespaces.add(name);
			scenarioData.put(CREATED_NAMESPACES, namespaces);
		}
	}
	
	@After
	public void deleteNamespaces() throws InterruptedException{
		if(scenarioData.containsKey(CREATED_NAMESPACES)){
			@SuppressWarnings("unchecked")
			List<String> namespaces = ((List<String>)scenarioData.get(CREATED_NAMESPACES));
			namespaces.forEach(kubeClient.namespaces()::delete);
			
			Awaitility.await().pollInterval(1, TimeUnit.SECONDS).atMost(2,TimeUnit.MINUTES)
				.until(()->kubeClient.namespaces().list().await().getNow().items().stream()
						.noneMatch(namespace->namespaces.contains(namespace.metadata().name())));
		}
	}
	
	@When("^I create an endpoint in namespace \"([^\"]*)\" with name \"([^\"]*)\" and address \"([^\"]*)\"$")
	public void iCreateAnEndpointInNamespaceWithNameAndAddress(String namespace, String name, String address) throws Exception {
		String[] addressParams = address.split(":");
		Future<Endpoint> result = kubeClient.endpoints().namespace(namespace).builder()
				.withName(name)
				.withSubset()
					.withAddress(addressParams[0].replace("${local.address}", getLocalAddress()))
					.withPort(Integer.parseInt(addressParams[1]))
					.create()
				.build().await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I update endpoint \"([^\"]*)\" in namespace \"([^\"]*)\" adding addresses \"([^\"]*)\"$")
	public void iUpdateEndpointInNamespaceAddingAddresses(String name, String namespace, String address) throws Exception {
		String[] addressParams = address.split(":");
		Future<Endpoint> result = kubeClient.endpoints().namespace(namespace).update(name)
				.addSubset()
					.withAddress(addressParams[0].replace("${local.address}", getLocalAddress()))
					.withPort(Integer.parseInt(addressParams[1]))
					.create()
			.update().await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I update endpoint \"([^\"]*)\" in namespace \"([^\"]*)\" setting addresses \"([^\"]*)\"$")
	public void iUpdateEndpointInNamespaceSettingAddresses(String name, String namespace, String address) throws Exception {
		String[] addressParams = address.split(":");
		Future<Endpoint> result = kubeClient.endpoints().namespace(namespace).update(name)
				.setSubset()
					.withAddress(addressParams[0].replace("${local.address}", getLocalAddress()))
					.withPort(Integer.parseInt(addressParams[1]))
					.create()
			.update().await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I create an ingress in namespace \"([^\"]*)\" with name \"([^\"]*)\", path \"([^\"]*)\" and backend service \"([^\"]*)\"$")
	public void iCreateAnIngressInNamespaceWithName(String namespace, String name, String path, String backend) throws Exception {
		String[] backendAddress = backend.split(":");
		Future<Ingress> result = kubeClient.ingresses().namespace(namespace).builder()
			.withName(name)
			.withRule()
				.withPath(path)
					.backendServiceName(backendAddress[0])
					.backendServicePort(Integer.parseInt(backendAddress[1]))
					.create()
				.create()
			.build().await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I create an ingress in namespace \"([^\"]*)\" with name \"([^\"]*)\", host \"([^\"]*)\", tls secret \"([^\"]*)\" and backend service \"([^\"]*)\"$")
	public void iCreateAnIngressInNamespaceWithNameHostTlsSecretAndBackendService(String namespace, String name, String host, String secret, String backend) throws Exception {
		String[] backendAddress = backend.split(":");
		Future<Ingress> result = kubeClient.ingresses().namespace(namespace).builder()
			.withName(name)
			.withTls()
				.withSecretName(secret)
				.withHost(host)
				.create()
			.withRule()
				.withHost(host)
				.withPath()
					.backendServiceName(backendAddress[0])
					.backendServicePort(Integer.parseInt(backendAddress[1]))
					.create()
				.create()
			.build().await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I create an ingress in namespace \"([^\"]*)\" with name \"([^\"]*)\", path \"([^\"]*)\", backend service \"([^\"]*)\" and annotations$")
	public void iCreateAnIngressInNamespaceWithNameAnnotations(String namespace, String name, String path, String backend, Map<String,String> annotations) throws Exception {
		String[] backendAddress = backend.split(":");
		IngressBuilder builder = kubeClient.ingresses().namespace(namespace).builder()
			.withName(name);
		annotations.forEach(builder::withAnnotation);
		Future<Ingress> result = builder
			.withRule()
				.withPath(path)
					.backendServiceName(backendAddress[0])
					.backendServicePort(Integer.parseInt(backendAddress[1]))
					.create()
				.create()
			.build().await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I create an ingress in namespace \"([^\"]*)\" with name \"([^\"]*)\" and backend service \"([^\"]*)\"$")
	public void iCreateAnIngressInNamespaceWithName(String namespace, String name, String backend) throws Exception {
		String[] backendAddress = backend.split(":");
		Future<Ingress> result = kubeClient.ingresses().namespace(namespace).builder()
			.withName(name)
			.withRule()
				.withPath()
					.backendServiceName(backendAddress[0])
					.backendServicePort(Integer.parseInt(backendAddress[1]))
					.create()
				.create()
			.build().await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I create an ingress in namespace \"([^\"]*)\" with name \"([^\"]*)\" and backend service \"([^\"]*)\" with port name \"([^\"]*)\"$")
	public void iCreateAnIngressInNamespaceWithNameAndBackendServiceWithPortName(String namespace, String name, String service, String portName) throws Exception {
		Future<Ingress> result = kubeClient.ingresses().namespace(namespace).builder()
			.withName(name)
			.withRule()
				.withPath()
					.backendServiceName(service)
					.backendServicePort(portName)
					.create()
				.create()
			.build().await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I update ingress \"([^\"]*)\" in namespace \"([^\"]*)\" adding path \"([^\"]*)\" and backend \"([^\"]*)\"$")
	public void iUpdateIngressInNamespaceAddingPathAndBackend(String name, String namespace, String path, String backend) throws Exception {
		String[] backendAddress = backend.split(":");
		Future<Ingress> result = kubeClient.ingresses().namespace(namespace).update(name)
				.addRule()
					.withPath()
						.backendServiceName(backendAddress[0])
						.backendServicePort(Integer.parseInt(backendAddress[1]))
						.create()
					.create()
				.update().await();
			assertTrue(result.isSuccess());
	}
	
	@When("^I create a service in namespace \"([^\"]*)\" with name \"([^\"]*)\" with port (\\d+) to target (\\d+)$")
	public void iCreateAServiceInNamespaceWithNameWithPortToTarget(String namespace, String name,int port, int target) throws Exception {
		Future<Service> result = kubeClient.services().namespace(namespace).builder()
				.withName(name)
				.withPort()
					.port(port)
					.targetPort(target)
					.create()
			.build().await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I create a service in namespace \"([^\"]*)\" with name \"([^\"]*)\" with port (\\d+), name \"([^\"]*)\" to target (\\d+)$")
	public void iCreateAServiceInNamespaceWithNameWithPortNameToTarget(String namespace, String name,int port, String portName, int target) throws Exception {
		Future<Service> result = kubeClient.services().namespace(namespace).builder()
				.withName(name)
				.withPort()
					.port(port)
					.name(portName)
					.targetPort(target)
					.create()
			.build().await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I update service \"([^\"]*)\" in namespace \"([^\"]*)\" setting port (\\d+) and target port (\\d+)$")
	public void iUpdateServiceInNamespaceSettingPortAndTargetPort(String name, String namespace, int port, int target) throws Exception {
		Future<Service> result = kubeClient.services().namespace(namespace).update(name)
				.setPort()
					.port(port)
					.targetPort(target)
					.create()
			.update().await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I update service \"([^\"]*)\" setting port (\\d+) to target (\\d+)$")
	public void iUpdateServiceSettingPort(String key, int port, int target) throws Exception {
		Service service = (Service) scenarioData.get(key);
		Future<Service> result = kubeClient.services().namespace(service.metadata().namespace()).update(service.metadata().name())
			.setPort()
				.port(port)
				.targetPort(target)
				.create()
			.update().await();
		assertTrue(result.isSuccess());
		scenarioData.put(key, result.getNow());
	}
	
	@When("^I create a service account in namespace \"([^\"]*)\" with name \"([^\"]*)\"$")
	public void iCreateAServiceAccountInNamespaceWithName(String namespace, String name) throws Exception {
		Future<ServiceAccount> result = kubeClient.serviceAccounts().namespace(namespace).builder()
			.withName(name)
			.build().await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I create a secret in namespace \"([^\"]*)\" with name \"([^\"]*)\" and next data:$")
	public void iCreateASecretInNamespaceWithNameAndNextData(String namespace, String name, Map<String,String> data) throws Exception {
		SecretBuilder builder = kubeClient.secrets().namespace(namespace).builder()
			.withName(name);
		data.forEach(builder::withData);
		Future<Secret> result = builder.build().await();
		assertTrue(result.isSuccess());
	}
	
	@Then("^I check that exist an endpoint \"([^\"]*)\" with name \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iCheckThatExistAnEndpointWithNameInNamespace(String key, String name, String namespace) throws Exception {
		Future<Endpoint> result = kubeClient.endpoints().namespace(namespace).get(name).await();
		assertTrue(result.isSuccess());
		assertThat(result.getNow(),notNullValue());
		scenarioData.put(key, result.getNow());
	}
	
	@Then("^I check that exist a service account \"([^\"]*)\" with name \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iCheckThatExistAServiceAccountWithNameInNamespace(String key, String name, String namespace) throws Exception {
		Future<ServiceAccount> result = kubeClient.serviceAccounts().namespace(namespace).get(name).await();
		assertTrue(result.isSuccess());
		assertThat(result.getNow(),notNullValue());
		scenarioData.put(key, result.getNow());
	}
	
	@Then("^I check that exist an ingress \"([^\"]*)\" with name \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iCheckThatExistAnIngressWithNameInNamespace(String key, String name, String namespace) throws Exception {
		Future<Ingress> result = kubeClient.ingresses().namespace(namespace).get(name).await();
		assertTrue(result.isSuccess());
		assertThat(result.getNow(),notNullValue());
		scenarioData.put(key, result.getNow());
	}
	
	@Then("^I check that exist a service \"([^\"]*)\" with name \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iCheckThatExistAServiceWithNameInNamespace(String key, String name, String namespace) throws Exception {
		Future<Service> result = kubeClient.services().namespace(namespace).get(name).await();
		assertTrue(result.isSuccess());
		assertThat(result.getNow(),notNullValue());
		scenarioData.put(key, result.getNow());
	}
	
	@Then("^I check that exist a secret \"([^\"]*)\" with name \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iCheckThatExistASecretWithNameInNamespace(String key, String name, String namespace) throws Exception {
		Future<Secret> result = kubeClient.secrets().namespace(namespace).get(name).await();
		assertTrue(result.isSuccess());
		assertThat(result.getNow(),notNullValue());
		scenarioData.put(key, result.getNow());
	}
	
	@Then("^I check that doesnt exist an endpoint \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iCheckThatDoesntExistAnEndpointInNamespace(String name, String namespace) throws Exception {
		Future<Endpoint> result = kubeClient.endpoints().namespace(namespace).get(name).await();
		assertFalse(result.isSuccess());
		assertThat(result.cause().getMessage(), equalTo("Unexpected code: 404"));
	}
	
	@Then("^I check that doesnt exist an ingress \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iCheckThatDoesntExistAnIngressInNamespace(String name, String namespace) throws Exception {
		Future<Ingress> result = kubeClient.ingresses().namespace(namespace).get(name).await();
		assertFalse(result.isSuccess());
		assertThat(result.cause().getMessage(), equalTo("Unexpected code: 404"));
	}
	
	@Then("^I check that doesnt exist a aservice \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iCheckThatDoesntExistAAserviceInNamespace(String name, String namespace) throws Exception {
		Future<Endpoint> result = kubeClient.endpoints().namespace(namespace).get(name).await();
		assertFalse(result.isSuccess());
		assertThat(result.cause().getMessage(), equalTo("Unexpected code: 404"));
	}
	
	@Then("^I check that doesnt exist a secret \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iCheckThatDoesntExistASecretInNamespace(String name, String namespace) throws Exception {
		Future<Secret> result = kubeClient.secrets().namespace(namespace).get(name).await();
		assertFalse(result.isSuccess());
		assertThat(result.cause().getMessage(), equalTo("Unexpected code: 404"));
	}
	
	@Then("^I check that doesnt exist a service account \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iCheckThatDoesntExistAServiceAccountInNamespace(String name, String namespace) throws Exception {
		Future<ServiceAccount> result = kubeClient.serviceAccounts().namespace(namespace).get(name).await();
		assertFalse(result.isSuccess());
		assertThat(result.cause().getMessage(), equalTo("Unexpected code: 404"));
	}
	
	@Then("^I check that endpoint \"([^\"]*)\" contains a subset with addresses \"([^\"]*)\" and ports \"([^\"]*)\"$")
	public void iCheckThatEndpointContainsASubsetWithAddressesAndPorts(String key, List<String> addresses, List<String> ports) throws Exception {
		Endpoint endpoint = (Endpoint) scenarioData.get(key);
		assertThat(endpoint.subsets(),hasSize(1));
		assertThat(Iterables.get(endpoint.subsets(), 0).addresses()
				.stream().map(Address::ip).collect(Collectors.toList()),
				contains(addresses.stream().map(Matchers::equalTo).collect(Collectors.toList())));
		
		assertThat(Iterables.get(endpoint.subsets(), 0).ports()
				.stream().map(Port::port).collect(Collectors.toList()),
				contains(ports.stream().map(port->Matchers.equalTo(Integer.parseInt(port))).collect(Collectors.toList())));
	}
	
	@Then("^I check that ingress \"([^\"]*)\" contains a path \"([^\"]*)\" with backend service \"([^\"]*)\"$")
	public void iCheckThatIngressContainsAPathWithBackendService(String key, String path, String backend) throws Exception {
		String[] backendAddress = backend.split(":");
		Ingress ingress = (Ingress) scenarioData.get(key);
		assertThat(ingress.spec().rules(),hasSize(1));
		IngressRule rule = ingress.spec().rules().get(0);
		assertThat(rule.http().paths(),hasSize(1));
		IngressPath ingressPath = rule.http().paths().get(0);
		assertThat(ingressPath.path(),equalTo(path));
		assertThat(ingressPath.backend().serviceName(),equalTo(backendAddress[0]));
		assertThat(ingressPath.backend().servicePort(),equalTo(Integer.parseInt(backendAddress[1])));
	}
	
	@When("^I check that ingress \"([^\"]*)\" contains (\\d+) rules$")
	public void iCheckThatIngressContainsRules(String key, int count) throws Exception {
		Ingress ingress = (Ingress) scenarioData.get(key);
		assertThat(ingress.spec().rules(),hasSize(count));
	}
	
	@When("^I update ingress \"([^\"]*)\" adding path \"([^\"]*)\" with backend service \"([^\"]*)\"$")
	public void iUpdateIngressAddingPathWithBackendService(String key, String path, String backend) throws Exception {
		String[] backendAddress = backend.split(":");
		Ingress ingress = (Ingress) scenarioData.get(key);
		Future<Ingress> result = kubeClient.ingresses().namespace(ingress.metadata().namespace()).update(ingress.metadata().name())
			.addRule()
				.withPath(path)
					.backendServiceName(backendAddress[0])
					.backendServicePort(Integer.parseInt(backendAddress[1]))
					.create()
				.create()
			.update().await();
		assertTrue(result.isSuccess());	
		scenarioData.put(key, result.getNow());
	}

	@When("^I delete endpoint \"([^\"]*)\"$")
	public void iDeleteEndpoint(String key) throws Throwable {
		Endpoint endpoint = (Endpoint) scenarioData.get(key);
		Future<Status> result = kubeClient.endpoints().namespace(endpoint.metadata().namespace()).delete(endpoint.metadata().name()).await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I delete endpoint \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iDeleteEndpointInNamespace(String name, String namespace) throws Throwable {
		Future<Status> result = kubeClient.endpoints().namespace(namespace).delete(name).await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I delete ingress \"([^\"]*)\"$")
	public void iDeleteIngress(String key) throws Throwable {
		Ingress ingress = (Ingress) scenarioData.get(key);
		Future<Status> result = kubeClient.ingresses().namespace(ingress.metadata().namespace()).delete(ingress.metadata().name()).await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I delete ingress \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iDeleteIngressInNamespace(String name,String namespace) throws Throwable {
		Future<Status> result = kubeClient.ingresses().namespace(namespace).delete(name).await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I delete service \"([^\"]*)\"$")
	public void iDeleteService(String key) throws Throwable {
		Service service = (Service) scenarioData.get(key);
		Future<Status> result = kubeClient.services().namespace(service.metadata().namespace()).delete(service.metadata().name()).await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I delete service \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iDeleteServiceInNamespace(String name, String namespace) throws Exception {
		Future<Status> result = kubeClient.services().namespace(namespace).delete(name).await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I delete secret \"([^\"]*)\"$")
	public void iDeleteSecret(String key) throws Exception {
		Secret secret = (Secret) scenarioData.get(key);
		Future<Status> result = kubeClient.secrets().namespace(secret.metadata().namespace()).delete(secret.metadata().name()).await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I delete service account \"([^\"]*)\"$")
	public void iDeleteServiceAccount(String key) throws Exception {
		ServiceAccount sa = (ServiceAccount) scenarioData.get(key);
		Future<Status> result = kubeClient.serviceAccounts().namespace(sa.metadata().namespace()).delete(sa.metadata().name()).await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I delete secret \"([^\"]*)\" in namespace \"([^\"]*)\"$")
	public void iDeleteSecretInNamespace(String name,String namespace) throws Throwable {
		Future<Status> result = kubeClient.secrets().namespace(namespace).delete(name).await();
		assertTrue(result.isSuccess());
	}
	
	@When("^I update endpoint \"([^\"]*)\" setting address \"([^\"]*)\"$")
	public void iUpdateEndpointSettingAddress(String key, String address) throws Exception {
		String[] addressParams = address.split(":");
		Endpoint endpoint = (Endpoint) scenarioData.get(key);
		Future<Endpoint> result = kubeClient.endpoints().namespace(endpoint.metadata().namespace()).update(endpoint.metadata().name())
			.setSubset()
				.withAddress(addressParams[0])
				.withPort(Integer.parseInt(addressParams[1]))
				.create()
			.update().await();
		assertTrue(result.isSuccess());
		scenarioData.put(key, result.getNow());
	}
	
	@When("^I watch services event getting \"([^\"]*)\" and an observable \"([^\"]*)\"$")
	public void iWatchServicesEventGettingAndAnObservable(String key, String observableKey) throws Exception {
		KubeList<Service> currentList = kubeClient.services().list().get();
		List<Event<Service>> events = new ArrayList<>();
		scenarioData.put(key, events);
		Observable<Service> observable = kubeClient.services().watch(currentList.metadata().resourceVersion()).onEvent(events::add);
		scenarioData.put(observableKey, observable);
	}
	
	@Then("^I check that events list \"([^\"]*)\" is empty$")
	public void iCheckThatEventsListIsEmpty(String key) throws Exception {
		List<?> list = (List<?>) scenarioData.get(key);
		assertThat(list, empty());
	}
	
	@Then("^I check that events list \"([^\"]*)\" contains (\\d+) event$")
	public void iCheckThatEventsListContainsEvent(String key, int size) throws Exception {
		List<?> list = (List<?>) scenarioData.get(key);
		Awaitility.await().until(()->list,hasSize(size));
		assertThat(list, hasSize(size));
	}
	
	@Then("^I check that events list \"([^\"]*)\" still containing (\\d+) event$")
	public void iCheckThatEventsListStillContainingEvent(String key, int size) throws Exception {
		List<?> list = (List<?>) scenarioData.get(key);
		Thread.sleep(1000);
		assertThat(list, hasSize(size));
	}

	@Then("^I check that events list \"([^\"]*)\" contains a \"([^\"]*)\" event$")
	public void iCheckThatEventsListContainsAEvent(String key, EventType eventType) throws Exception {
		List<?> list = (List<?>) scenarioData.get(key);
		Event<?> event = (Event<?>) list.get(0);
		assertThat(event.type(),equalTo(eventType));
	}
	
	@Then("^I check that events list \"([^\"]*)\" contains a \"([^\"]*)\" event in index (\\d+)$")
	public void iCheckThatEventsListContainsAEventInIndex(String key, EventType eventType, int index) throws Exception {
		List<?> list = (List<?>) scenarioData.get(key);
		Event<?> event = (Event<?>) list.get(index);
		assertThat(event.type(),equalTo(eventType));
	}
	
	@When("^I stop watchin the observable \"([^\"]*)\"$")
	public void iStopWatchinTheObservable(String key) throws Exception {
		Observable<?> observable = (Observable<?>) scenarioData.get(key);
		observable.close().await();
	}
	
	@Then("^I check that service \"([^\"]*)\" is listening on port (\\d+) to target (\\d+)$")
	public void iCheckThatServiceIsListeningOnPortToTarget(String key, int port, int target) throws Exception {
		Service service = (Service) scenarioData.get(key);
		assertThat(service.spec().ports(), hasItem(allOf(
				new FunctionMatcher<ServicePort,Integer>(s->s.port(),equalTo(port)),
				new FunctionMatcher<ServicePort,Object>(s->s.targetPort(),equalTo(target))
				)));
	}
	
	public static String getLocalAddress()  {
		Enumeration<NetworkInterface> nets = Try.of(NetworkInterface::getNetworkInterfaces).get();
	    for (NetworkInterface netint : Collections.list(nets)) {
	    		Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
	    		for (InetAddress inetAddress : Collections.list(inetAddresses)) {
	    			if(inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
	    				return inetAddress.getHostAddress();
	    			}
	    		}
	    }
	    return null;
	}
	
}
