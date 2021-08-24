package com.simplyti.service.gateway.balancer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.simplyti.service.clients.endpoint.Endpoint;

import lombok.ToString;

@ToString(of="endpoints")
public class RoundRobinLoadBalancer implements ServiceBalancer {
	
	private final List<Endpoint> endpoints;
	private int position = 0;
	
	private RoundRobinLoadBalancer() {
		this.endpoints = Collections.emptyList();
	}

	private RoundRobinLoadBalancer(List<Endpoint> endpoints) {
		this.endpoints=endpoints;
	}
	
	@Override
	public Collection<Endpoint> endpoints(){
		return endpoints;
	}

	@Override
	public synchronized Endpoint next() {
        if (position > endpoints.size() - 1) {
            position = 0;
        }
        final Endpoint target = endpoints.get(position);
        position++;
        return target;
	}
	
	public static ServiceBalancer of(List<Endpoint> endpoints) {
		if(endpoints==null || endpoints.isEmpty()) {
			return Empty.INSTANCE;
		} else if (endpoints.size() == 1) {
			return new Single(Iterables.get(endpoints, 0));
		} else {
			return new RoundRobinLoadBalancer(endpoints);
		}
	}

	@Override
	public ServiceBalancer add(Endpoint endpoint) {
		return of(ImmutableList.<Endpoint>builder().addAll(endpoints).add(endpoint).build());
	}

	@Override
	public ServiceBalancer delete(Endpoint endpoint) {
		return of(endpoints.stream().filter(edp->!edp.equals(endpoint))
				.collect(ImmutableList.toImmutableList()));
	}

	@Override
	public ServiceBalancer clear() {
		return Empty.INSTANCE;
	}
	
	private static class Empty extends RoundRobinLoadBalancer {
		
		private static final ServiceBalancer INSTANCE = new Empty();

		@Override
		public Endpoint next() {
			return null;
		}

	}
	
	private static class Single extends RoundRobinLoadBalancer {
		
		private final Endpoint endpoint;

		public Single(Endpoint endpoint) {
			super(Collections.singletonList(endpoint));
			this.endpoint=endpoint;
		}

		@Override
		public Endpoint next() {
			return endpoint;
		}

	}
	
}
