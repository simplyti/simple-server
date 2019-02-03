package com.simplyti.service.clients.k8s.ingresses.updater;

import com.simplyti.service.clients.k8s.ingresses.builder.IngressRuleBuilder;
import com.simplyti.service.clients.k8s.ingresses.domain.Ingress;

import io.netty.util.concurrent.Future;

public interface IngressUpdater {

	IngressRuleBuilder<? extends IngressUpdater> addRule();

	Future<Ingress> update();

}
