package com.simplyti.service.clients.http.handler;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.HttpClientStreamEvent;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;

public abstract class AbstractStreamedResponseHandler<T> extends AbstractBaseHttpResponseHandler<T> implements ClientChannelInitializerHandler {
	
	private final ClientChannel channel;
	private final Promise<Void> promise;
	private final Consumer<T> consumer;
	
	private int customHandlerCount = 0;

	public AbstractStreamedResponseHandler(ClientChannel channel, Promise<Void> promise, Consumer<T> consumer) {
		super(channel,promise);
		this.channel=channel;
		this.promise=promise;
		this.consumer=consumer;
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		for(int i=customHandlerCount-1 ; i>=0; i--) {
			channel.pipeline().remove("stream.custom.handler."+i);
		}
	}
	
	@Override
	public ClientChannelInitializerHandler addLast(ChannelHandler handler) {
		channel.pipeline().addLast("stream.custom.handler."+customHandlerCount++,handler);
		return this;
	}
	

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception {
		consumer.accept(msg);
		ReferenceCountUtil.release(msg);
	}
	
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof HttpClientStreamEvent && ((HttpClientStreamEvent) evt).type() == HttpClientStreamEvent.Type.STOP) {
			channel.pipeline().remove(this);
			channel.release();
			promise.trySuccess(null);
		}
		ctx.fireUserEventTriggered(evt);
    }
	
}
