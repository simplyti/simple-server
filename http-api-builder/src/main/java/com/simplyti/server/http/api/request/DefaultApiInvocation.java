package com.simplyti.server.http.api.request;


import com.simplyti.server.http.api.handler.ApiInvocation;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.Recycler;
import io.netty.util.Recycler.Handle;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class DefaultApiInvocation implements ApiInvocation {
	
	private static final Recycler<DefaultApiInvocation> RECYCLER = new Recycler<DefaultApiInvocation>() {
        @Override
        public DefaultApiInvocation newObject(Handle<DefaultApiInvocation> handle) {
            return new DefaultApiInvocation(handle);
        }
    };
	
	/**
     * Create a new empty {@link FullApiInvocation} instance
     */
    public static DefaultApiInvocation newInstance(HttpRequest request, ApiMatchRequest match) {
    	DefaultApiInvocation apiReuest = RECYCLER.get();
    	apiReuest.request = request;
    	apiReuest.match = match;
        return apiReuest;
    }


    private final Handle<DefaultApiInvocation> handle;
	private HttpRequest request;
	private ApiMatchRequest match;
	
	private DefaultApiInvocation(Handle<DefaultApiInvocation> handle) {
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
