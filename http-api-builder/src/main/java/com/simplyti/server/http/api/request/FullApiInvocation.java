package com.simplyti.server.http.api.request;


import com.simplyti.server.http.api.handler.ApiInvocation;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.ObjectPool.Handle;
import io.netty.util.internal.ObjectPool.ObjectCreator;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class FullApiInvocation implements ApiInvocation {
	
	private static final ObjectPool<FullApiInvocation> RECYCLER = ObjectPool.newPool(new ObjectCreator<FullApiInvocation>() {
        @Override
        public FullApiInvocation newObject(Handle<FullApiInvocation> handle) {
            return new FullApiInvocation(handle);
        }
    });
	
	/**
     * Create a new empty {@link RecyclableArrayList} instance
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

//	public FullApiInvocation(ApiMatchRequest apiMatch, FullHttpRequest request) {
//		this.match=apiMatch;
//		this.request=request;
//	}

//	@Override
//	public HttpHeaders headers() {
//		return request.headers();
//	}

//	@Override
//	public int refCnt() {
//		return request.refCnt();
//	}
//
//	@Override
//	public ReferenceCounted retain() {
//		request.retain();
//		return this;
//	}
//
//	@Override
//	public ReferenceCounted retain(int increment) {
//		request.retain(increment);
//		return this;
//	}
//
//	@Override
//	public ReferenceCounted touch() {
//		request.touch();
//		return this;
//	}
//
//	@Override
//	public ReferenceCounted touch(Object hint) {
//		request.touch(hint);
//		return this;
//	}
//
//	@Override
//	public boolean release() {
//		return request.release();
//	}
//
//	@Override
//	public boolean release(int decrement) {
//		return request.release(decrement);
//	}

}
