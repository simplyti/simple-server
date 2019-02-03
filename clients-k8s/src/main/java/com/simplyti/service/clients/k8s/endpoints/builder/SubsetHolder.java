package com.simplyti.service.clients.k8s.endpoints.builder;

import com.simplyti.service.clients.k8s.endpoints.domain.Subset;

public interface SubsetHolder<T extends SubsetHolder<T>> {

	T addSubset(Subset subset);

}
