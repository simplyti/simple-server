package com.simplyti.service.gateway.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.simplyti.server.http.api.ApiProvider;
import com.simplyti.server.http.api.builder.ApiBuilder;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.gateway.ServiceDiscovery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor=@__(@Inject))
public class GatewayApi implements ApiProvider{
	
	private final ServiceDiscovery serviceDiscovery;

	@Override
	public void build(ApiBuilder builder) {
		builder.when().get("/_api/gateway/services")
			.withResponseBodyType(new TypeLiteral<List<GatewayService>>() {})
			.then(ctx->{
				ctx.send(serviceDiscovery.services().stream()
						.map(service->GatewayService.builder()
								.endpoints(service.loadBalander().endpoints().stream().
										map(ep->GatewayEndpoint.builder().url(ep.toString()).build())
										.collect(Collectors.toList()))
								.build())
						.collect(Collectors.toList()));
			});
		
	}

}
