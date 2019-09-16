package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.HookHandler;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class HookHandlerHolder {

    private HookHandler handler;

    public void setHookHandler(HookHandler handler) {
        this.handler = handler;
    };
}
