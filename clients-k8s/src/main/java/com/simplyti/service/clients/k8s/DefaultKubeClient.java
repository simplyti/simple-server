package com.simplyti.service.clients.k8s;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.endpoint.TcpAddress;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.clients.k8s.configmaps.ConfigMaps;
import com.simplyti.service.clients.k8s.configmaps.DefaultConfigMaps;
import com.simplyti.service.clients.k8s.endpoints.DefaultEndpoints;
import com.simplyti.service.clients.k8s.endpoints.Endpoints;
import com.simplyti.service.clients.k8s.ingresses.DefaultIngresses;
import com.simplyti.service.clients.k8s.ingresses.Ingresses;
import com.simplyti.service.clients.k8s.jobs.DefaultJobs;
import com.simplyti.service.clients.k8s.jobs.Jobs;
import com.simplyti.service.clients.k8s.namespaces.DefaultNamespaces;
import com.simplyti.service.clients.k8s.namespaces.Namespaces;
import com.simplyti.service.clients.k8s.pods.DefaultPods;
import com.simplyti.service.clients.k8s.pods.Pods;
import com.simplyti.service.clients.k8s.secrets.DefaultSecrets;
import com.simplyti.service.clients.k8s.secrets.Secrets;
import com.simplyti.service.clients.k8s.serviceaccounts.DefaultServiceAccounts;
import com.simplyti.service.clients.k8s.serviceaccounts.ServiceAccounts;
import com.simplyti.service.clients.k8s.services.DefaultServices;
import com.simplyti.service.clients.k8s.services.Services;
import com.simplyti.service.serializer.json.DslJsonSerializer;

import io.netty.channel.EventLoopGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent=true)
public class DefaultKubeClient implements KubeClient {
	
	private static final String DEFAULT_TOKEN_FILE = "/var/run/secrets/kubernetes.io/serviceaccount/token";

	@Getter
	private final Pods pods;
	
	@Getter
	private final Jobs jobs;
	
	@Getter
	private final Services services;
	
	@Getter
	private final Ingresses ingresses;
	
	@Getter
	private final Endpoints endpoints;
	
	@Getter
	private final Secrets secrets;
	
	@Getter
	private final ServiceAccounts serviceAccounts;
	
	@Getter
	private final Namespaces namespaces;

	@Getter
	private final ConfigMaps configMaps;
	
	private final HttpClient http;


	public DefaultKubeClient(EventLoopGroup eventLoopGroup, Endpoint endpoint, String token) {
		long timeoutMillis = 30000;
		this.http = HttpClient.builder()
				.withReadTimeout(timeoutMillis)
				.withEventLoopGroup(eventLoopGroup)
				.withCheckStatusCode()
				.withEndpoint(apiserver(endpoint))
				.withBearerAuth(token!=null?token:readFile(DEFAULT_TOKEN_FILE))
				.build();
		Json json = new DslJsonSerializer(Collections.singleton(new K8sDomainConfiguration()));
		this.pods = new DefaultPods(http.eventLoopGroup(),http,timeoutMillis,json);
		this.jobs = new DefaultJobs(http.eventLoopGroup(),http,timeoutMillis,json);
		this.services = new DefaultServices(http.eventLoopGroup(),http,timeoutMillis,json);
		this.ingresses = new DefaultIngresses(http.eventLoopGroup(),http,timeoutMillis,json);
		this.endpoints = new DefaultEndpoints(http.eventLoopGroup(),http,timeoutMillis,json);
		this.secrets = new DefaultSecrets(http.eventLoopGroup(),http,timeoutMillis,json);
		this.serviceAccounts = new DefaultServiceAccounts(http.eventLoopGroup(),http,timeoutMillis,json);
		this.namespaces = new DefaultNamespaces(http.eventLoopGroup(),http,timeoutMillis,json);
		this.configMaps = new DefaultConfigMaps(http.eventLoopGroup(), http,timeoutMillis, json);
	}

	private String readFile(String fileName) {
		Path path = Paths.get(fileName);
		if(Files.exists(path)){
			try(BufferedReader br = new BufferedReader(new FileReader(path.toFile()))){
				return  br.readLine();
			} catch(Throwable e) {
				return null;
			}
		}else {
			return null;
		}
	}

	private Endpoint apiserver(Endpoint endpoint) {
		if(endpoint!=null) {
			return endpoint;
		}
		return new HttpEndpoint(HttpEndpoint.HTTPS_SCHEMA, new TcpAddress("kubernetes.default",HttpEndpoint.HTTPS_SCHEMA.defaultPort()),null);
	}

	@Override
	public Future<String> health() {
		return  http.request().get("/healthz")
					.fullResponse(response->response.content().toString(CharsetUtil.UTF_8));
	}

	@Override
	public NamespacedClient namespace(String name) {
		return new DefaultNamespacedClient(name,this);
	}

}
