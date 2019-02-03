package com.simplyti.service.clients.k8s.serviceaccounts;

import com.simplyti.service.clients.k8s.common.NamespacedK8sApi;
import com.simplyti.service.clients.k8s.serviceaccounts.builder.ServiceAccountBuilder;
import com.simplyti.service.clients.k8s.serviceaccounts.domain.ServiceAccount;

public interface NamespacedServiceAccounts extends NamespacedK8sApi<ServiceAccount> {

	ServiceAccountBuilder builder();

}