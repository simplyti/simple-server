package com.simplyti.server.http.api.handler.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.DefaultByteBufHolder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class ApiBufferResponse extends ApiResponse implements ByteBufHolder {

	private final ByteBuf message;

	public ApiBufferResponse(ByteBuf content, boolean keepAlive, boolean notFoundOnNull) {
		super(keepAlive, notFoundOnNull);
		this.message=content;
	}
	
	@Override
    public ByteBuf content() {
        return ByteBufUtil.ensureAccessible(message);
    }

    @Override
    public ByteBufHolder copy() {
        return replace(message.copy());
    }

    @Override
    public ByteBufHolder duplicate() {
        return replace(message.duplicate());
    }

    @Override
    public ByteBufHolder retainedDuplicate() {
        return replace(message.retainedDuplicate());
    }

    @Override
    public ByteBufHolder replace(ByteBuf content) {
        return new DefaultByteBufHolder(content);
    }

    @Override
    public int refCnt() {
        return message.refCnt();
    }

    @Override
    public ByteBufHolder retain() {
    	message.retain();
        return this;
    }

    @Override
    public ByteBufHolder retain(int increment) {
    	message.retain(increment);
        return this;
    }

    @Override
    public ByteBufHolder touch() {
    	message.touch();
        return this;
    }

    @Override
    public ByteBufHolder touch(Object hint) {
    	message.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return message.release();
    }

    @Override
    public boolean release(int decrement) {
        return message.release(decrement);
    }
	
	
}
