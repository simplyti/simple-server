package com.simplyti.service.clients.k8s;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import com.jsoniter.spi.JsoniterSpi;
import com.simplyti.service.clients.Address;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.clients.k8s.endpoints.DefaultEndpoints;
import com.simplyti.service.clients.k8s.endpoints.Endpoints;
import com.simplyti.service.clients.k8s.ingresses.DefaultIngresses;
import com.simplyti.service.clients.k8s.ingresses.Ingresses;
import com.simplyti.service.clients.k8s.namespaces.DefaultNamespaces;
import com.simplyti.service.clients.k8s.namespaces.Namespaces;
import com.simplyti.service.clients.k8s.pods.DefaultPods;
import com.simplyti.service.clients.k8s.pods.Pods;
import com.simplyti.service.clients.k8s.secrets.DefaultSecrets;
import com.simplyti.service.clients.k8s.secrets.Secrets;
import com.simplyti.service.clients.k8s.secrets.domain.SecretData;
import com.simplyti.service.clients.k8s.serviceaccounts.DefaultServiceAccounts;
import com.simplyti.service.clients.k8s.serviceaccounts.ServiceAccounts;
import com.simplyti.service.clients.k8s.services.DefaultServices;
import com.simplyti.service.clients.k8s.services.Services;

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
	
	private final HttpClient http;


	public DefaultKubeClient(EventLoopGroup eventLoopGroup, String endpoint, String token) {
		this.http = HttpClient.builder()
				.eventLoopGroup(eventLoopGroup)
				.withCheckStatusCode()
				.withEndpoint(apiserver(endpoint))
				.withBearerAuth(token!=null?token:readFile(DEFAULT_TOKEN_FILE))
				.build();
		this.pods = new DefaultPods(eventLoopGroup,http);
		this.services = new DefaultServices(eventLoopGroup,http);
		this.ingresses = new DefaultIngresses(eventLoopGroup,http);
		this.endpoints = new DefaultEndpoints(eventLoopGroup,http);
		this.secrets = new DefaultSecrets(eventLoopGroup,http);
		this.serviceAccounts = new DefaultServiceAccounts(eventLoopGroup,http);
		this.namespaces = new DefaultNamespaces(eventLoopGroup,http);
		
		JsoniterSpi.registerTypeEncoder(SecretData.class, (value,stream)->stream.writeVal(Base64.getEncoder().encodeToString(SecretData.class.cast(value).getData())));
		JsoniterSpi.registerTypeDecoder(SecretData.class, iter->SecretData.of(Base64.getDecoder().decode(iter.readString().getBytes(CharsetUtil.UTF_8))));
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

	private Endpoint apiserver(String endpoint) {
		if(endpoint!=null) {
			return HttpEndpoint.of(endpoint);
		}
		return new HttpEndpoint(HttpEndpoint.HTTPS_SCHEMA, new Address("kubernetes.default",HttpEndpoint.HTTPS_SCHEMA.defaultPort()),null);
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
