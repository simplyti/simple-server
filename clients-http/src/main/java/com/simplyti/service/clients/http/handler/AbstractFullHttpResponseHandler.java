package com.simplyti.service.clients.http.handler;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.exception.HttpException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.util.concurrent.Promise;

public abstract class AbstractFullHttpResponseHandler<T> extends AbstractBaseHttpResponseHandler<FullHttpResponse> {

	private final ClientChannel channel;
	private final Promise<T> promise;
	private final boolean checkStatus;

	public AbstractFullHttpResponseHandler(ClientChannel channel, Promise<T> promise, boolean checkStatus) {
		super(channel,promise);
		this.channel=channel;
		this.promise=promise;
		this.checkStatus=checkStatus;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
		channel.pipeline().remove(this);
		channel.release();
		if(checkStatus && isError(msg.status().codeClass())) {
			msg.release();
			promise.setFailure(new HttpException(msg.status().code()));
		} else {
			try{
				promise.setSuccess(handle(msg));
			} catch (Throwable cause) {
				exceptionCaught(ctx, cause);
			}
		}
	}
	
	private boolean isError(HttpStatusClass codeClass) {
		return codeClass.equals(HttpStatusClass.CLIENT_ERROR) || 
				codeClass.equals(HttpStatusClass.SERVER_ERROR);
	}
	
	protected abstract T handle(FullHttpResponse msg);

}
