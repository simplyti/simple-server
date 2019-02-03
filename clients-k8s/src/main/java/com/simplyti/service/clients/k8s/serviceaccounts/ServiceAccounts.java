package com.simplyti.service.clients.k8s.serviceaccounts;

import com.simplyti.service.clients.k8s.common.K8sApi;
import com.simplyti.service.clients.k8s.common.Namespaced;
import com.simplyti.service.clients.k8s.serviceaccounts.builder.ServiceAccountBuilder;
import com.simplyti.service.clients.k8s.serviceaccounts.domain.ServiceAccount;

public interface ServiceAccounts extends Namespaced<ServiceAccount,NamespacedServiceAccounts>, K8sApi<ServiceAccount>{

	ServiceAccountBuilder builder(String namespace);

}
