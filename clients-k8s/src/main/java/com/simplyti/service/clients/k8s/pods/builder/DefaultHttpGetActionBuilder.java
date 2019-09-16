package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.clients.k8s.pods.domain.HttpGetActionHookHandler;
import com.simplyti.service.clients.k8s.pods.domain.HttpHeader;

import java.util.ArrayList;
import java.util.List;

public class DefaultHttpGetActionBuilder<T> implements HttpGetActionBuilder<T> {

    private HttpEndpoint endpoint;
    private List<HttpHeader> headers = new ArrayList<>();

    private final LifecycleBuilder<T> parent;
    private final HookHandlerHolder holder;

    public DefaultHttpGetActionBuilder(final LifecycleBuilder<T> parent, final HookHandlerHolder holder) {
        this.parent = parent;
        this.holder = holder;
    }

    @Override
    public HttpGetActionBuilder<T> endpoint(String endpoint) {
        this.endpoint = HttpEndpoint.of(endpoint);
        return null;
    }

    @Override
    public HttpGetActionBuilder<T> addHeaders(String name, String value) {
        this.headers.add(new HttpHeader(name, value));
        return null;
    }

    @Override
    public LifecycleBuilder<T> end() {
        this.holder.setHookHandler(new HttpGetActionHookHandler(
                endpoint.schema().name(),
                endpoint.address().host(),
                endpoint.address().port(),
                endpoint.path(),
                this.headers));
        return this.parent;
    }
}
