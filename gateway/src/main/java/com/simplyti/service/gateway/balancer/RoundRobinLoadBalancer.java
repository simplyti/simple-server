package com.simplyti.service.gateway.balancer;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.simplyti.service.clients.Endpoint;

import lombok.ToString;

@ToString(of="endpoints")
public class RoundRobinLoadBalancer implements ServiceBalancer {
	
	private static final RoundRobinLoadBalancer EMPTY = new RoundRobinLoadBalancer(null);

	private final Iterator<Endpoint> iterator;
	private final Collection<Endpoint> endpoints;

	public RoundRobinLoadBalancer(Collection<Endpoint> endpoints) {
		this.endpoints=MoreObjects.firstNonNull(endpoints, Collections.emptyList());
		this.iterator = Iterables.cycle(this.endpoints).iterator();
	}
	
	@Override
	public Collection<Endpoint> endpoints(){
		return endpoints;
	}

	@Override
	public Endpoint next() {
		try {
			return iterator.next();
		}catch (NoSuchElementException ex) {
			return null;
		}
	}

	@Override
	public ServiceBalancer add(Endpoint endpoint) {
		return new RoundRobinLoadBalancer(ImmutableSet.<Endpoint>builder().addAll(endpoints).add(endpoint).build());
	}

	@Override
	public ServiceBalancer delete(Endpoint endpoint) {
		return new RoundRobinLoadBalancer(endpoints.stream().filter(edp->!edp.equals(endpoint))
				.collect(ImmutableSet.toImmutableSet()));
	}

	@Override
	public ServiceBalancer clear() {
		return EMPTY;
	}

}
