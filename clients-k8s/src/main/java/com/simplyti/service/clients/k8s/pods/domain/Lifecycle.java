package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Lifecycle {

	private final LifecycleHookHandler postStart;
    private final LifecycleHookHandler preStop;

    @CompiledJson
    public Lifecycle(final LifecycleHookHandler postStart,final LifecycleHookHandler preStop) {
        this.preStop = preStop;
        this.postStart = postStart;
    }

}
