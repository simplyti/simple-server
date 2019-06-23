package com.simplyti.service.api;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.jsoniter.JsonIterator;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sse.DefaultSSEStream;
import com.simplyti.service.sse.SSEStream;
import com.simplyti.service.sse.ServerSentEventEncoder;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class DefaultApiInvocationContext<I,O> extends AbstractApiInvocationContext<O>  implements ApiInvocationContext<I,O>, Supplier<I>, ByteBufHolder{
	
	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	private final ChannelHandlerContext ctx;
	private final FullApiInvocation<I> msg;
	private final Supplier<I> cachedRequestBody;
	private final ServerSentEventEncoder serverSentEventEncoder;
	
	private boolean released = false ;
	
	private final ByteBuf data;
	
	public DefaultApiInvocationContext(ChannelHandlerContext ctx, ApiMacher matcher, FullApiInvocation<I> msg, 
			ExceptionHandler exceptionHandler, ServerSentEventEncoder serverSentEventEncoder, SyncTaskSubmitter syncTaskSubmitter) {
		super(ctx,matcher,msg,exceptionHandler,syncTaskSubmitter);
		this.data=msg.content();
		this.ctx=ctx;
		this.msg=msg;
		this.cachedRequestBody=Suppliers.memoize(this);
		this.serverSentEventEncoder=serverSentEventEncoder;
	}
	
	@Override
	public I body() {
		return cachedRequestBody.get();
	}
	
	
	private void release0() {
		release();
		released=true;
	}

	
	public void tryRelease() {
		if(!released) {
			release0();
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public I get() {
		final I result;
		if(msg.operation().requestType().getType().equals(ByteBuf.class)){
			result =  (I) content();
		} else if(!content().isReadable() || 
				msg.operation().requestType().getType().equals(Void.class)){
			result = null;
			release0();
		} else if(msg.operation().requestType().getType().equals(String.class)){
			result = (I) content().toString(CharsetUtil.UTF_8);
			release0();
		} else if(msg.operation().isMultipart()){
			result = decodeMultipart();
		} else{
			byte[] data = new byte[content().readableBytes()];
			content().readBytes(data);
			result = JsonIterator.deserialize(data).as(msg.operation().requestType());
			release0();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private I decodeMultipart() {
		HttpPostMultipartRequestDecoder decoder = new HttpPostMultipartRequestDecoder(msg.request());
		List<com.simplyti.service.api.multipart.FileUpload> files = decoder.getBodyHttpDatas().stream()
			.filter(data->data.getHttpDataType().equals(HttpDataType.FileUpload))
			.map(FileUpload.class::cast)
			.map(data->new com.simplyti.service.api.multipart.FileUpload(data.content().retain(),data.getFilename()))
			.collect(Collectors.toList());
		try{
			decoder.destroy();
		}catch(Throwable e) {
			log.warn("Cannot destroy multipart decoder: {}",e.getMessage());
		}
		return (I) files;
	}


	@Override
	public SSEStream sse() {
		tryRelease();
		return new DefaultSSEStream(ctx,serverSentEventEncoder);
	}
	
    @Override
    public ByteBuf content() {
        if (data.refCnt() <= 0) {
            throw new IllegalReferenceCountException(data.refCnt());
        }
        return data;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method calls {@code replace(content().copy())} by default.
     */
    @Override
    public ByteBufHolder copy() {
        return replace(data.copy());
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method calls {@code replace(content().duplicate())} by default.
     */
    @Override
    public ByteBufHolder duplicate() {
        return replace(data.duplicate());
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method calls {@code replace(content().retainedDuplicate())} by default.
     */
    @Override
    public ByteBufHolder retainedDuplicate() {
        return replace(data.retainedDuplicate());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override this method to return a new instance of this object whose content is set to the specified
     * {@code content}. The default implementation of {@link #copy()}, {@link #duplicate()} and
     * {@link #retainedDuplicate()} invokes this method to create a copy.
     */
    @Override
    public ByteBufHolder replace(ByteBuf content) {
        return new DefaultByteBufHolder(content);
    }

    @Override
    public int refCnt() {
        return data.refCnt();
    }

    @Override
    public ByteBufHolder retain() {
        data.retain();
        return this;
    }

    @Override
    public ByteBufHolder retain(int increment) {
        data.retain(increment);
        return this;
    }

    @Override
    public ByteBufHolder touch() {
        data.touch();
        return this;
    }

    @Override
    public ByteBufHolder touch(Object hint) {
        data.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return data.release();
    }

    @Override
    public boolean release(int decrement) {
        return data.release(decrement);
    }

    /**
     * Return {@link ByteBuf#toString()} without checking the reference count first. This is useful to implement
     * {@link #toString()}.
     */
    protected final String contentToString() {
        return data.toString();
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '(' + contentToString() + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ByteBufHolder) {
            return data.equals(((ByteBufHolder) o).content());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

}
