package com.simplyti.server.http.api.request;


import com.simplyti.server.http.api.handler.ApiInvocation;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.Recycler;
import io.netty.util.Recycler.Handle;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class FullApiInvocation implements ApiInvocation {
	
	private static final Recycler<FullApiInvocation> RECYCLER = new Recycler<FullApiInvocation>() {
        @Override
        public FullApiInvocation newObject(Handle<FullApiInvocation> handle) {
            return new FullApiInvocation(handle);
        }
    };
	
	/**
     * Create a new empty {@link FullApiInvocation} instance
     */
    public static FullApiInvocation newInstance(FullHttpRequest request, ApiMatchRequest match) {
    	FullApiInvocation apiReuest = RECYCLER.get();
    	apiReuest.request = request;
    	apiReuest.match = match;
        return apiReuest;
    }


    private final Handle<FullApiInvocation> handle;
	private FullHttpRequest request;
	private ApiMatchRequest match;
	
	private FullApiInvocation(Handle<FullApiInvocation> handle) {
        this.handle = handle;
    }
	
	/**
     * Clear and recycle this instance.
     */
    public boolean recycle() {
    	request = null;
    	match = null;
        handle.recycle(this);
        return true;
    }

}
