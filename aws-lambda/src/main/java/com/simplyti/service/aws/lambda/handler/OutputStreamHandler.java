package com.simplyti.service.aws.lambda.handler;

import java.io.OutputStream;

import com.simplyti.service.aws.lambda.LambdaChannelPool;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Promise;

public class OutputStreamHandler extends ChannelOutboundHandlerAdapter {

	private final OutputStream outputStream;
	private final LambdaChannelPool embededChannelPool;
	private final Promise<Void> resultPromise;

	public OutputStreamHandler(LambdaChannelPool embededChannelPool, OutputStream outputStream, Promise<Void> promise) {
		this.outputStream=outputStream;
		this.embededChannelPool=embededChannelPool;
		this.resultPromise=promise;
	}

	@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		ByteBuf data = (ByteBuf) msg;
		byte[] bytes = new byte[data.readableBytes()];
		data.readBytes(bytes);
		data.release();
		outputStream.write(bytes);
		outputStream.close();
		resultPromise.setSuccess(null);
		promise.setSuccess();
		ctx.channel().pipeline().remove(this);
		embededChannelPool.offer(ctx.channel());
    }

}
