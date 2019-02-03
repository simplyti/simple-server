package com.simplyti.service.clients.k8s.endpoints.builder;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.Metadata;
import com.simplyti.service.clients.k8s.common.builder.AbstractK8sResourceBuilder;
import com.simplyti.service.clients.k8s.endpoints.domain.Endpoint;
import com.simplyti.service.clients.k8s.endpoints.domain.Subset;

public class DefaultEndpointBuilder extends AbstractK8sResourceBuilder<EndpointBuilder,Endpoint> implements EndpointBuilder, SubsetHolder<DefaultEndpointBuilder> {

	public static final String KIND = "Endpoints";
	
	private List<Subset> subsets = new ArrayList<>();
	
	public DefaultEndpointBuilder(HttpClient client, K8sAPI api, String namespace, String resource) {
		super(client, api, namespace, resource, Endpoint.class);
	}

	@Override
	public EndpointSubsetBuilder<? extends EndpointBuilder> withSubset() {
		return new DefaultEndpointSubsetBuilder<>(this);
	}
	
	@Override
	public DefaultEndpointBuilder addSubset(Subset subset) {
		subsets.add(subset);
		return this;
	}

	@Override
	protected Endpoint resource(K8sAPI api, Metadata metadata) {
		return new Endpoint(KIND, api.version(), metadata, subsets);
	}

	


}
