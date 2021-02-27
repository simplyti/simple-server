package com.simplyti.service.clients.http.handler;

import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;

import com.simplyti.service.clients.channel.ClientChannel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.concurrent.Promise;

public abstract class AbstractBaseHttpResponseHandler<T> extends SimpleChannelInboundHandler<T> implements ChannelOutboundHandler {
	
	private final ClientChannel channel;
	private final Promise<?> promise;
	private final ByteBuf content;
	
	private boolean lastOutput;
	private boolean lastInput;

	protected AbstractBaseHttpResponseHandler(ClientChannel channel, ByteBuf content, Promise<?> promise) {
		super(false);
		this.channel=channel;
		this.promise=promise;
		this.content=content;
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		promise.tryFailure(new ClosedChannelException());
		channel.release();
    }
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		if(!ctx.channel().isActive()) {
			channelInactive(ctx);
		}
    }
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		promise.tryFailure(cause);
		channel.close().addListener(f->channel.release());
    }
	
	@Override
	public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
		ctx.bind(localAddress, promise);
	}

	@Override
	public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress,
			ChannelPromise promise) throws Exception {
		ctx.connect(remoteAddress, localAddress, promise);
	}

	@Override
	public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
		ctx.disconnect(promise);
	}

	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
		ctx.close(promise);
	}

	@Override
	public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
		ctx.deregister(promise);
	}

	@Override
	public void read(ChannelHandlerContext ctx) throws Exception {
		ctx.read();
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		ctx.write(msg, promise);
		if(msg instanceof LastHttpContent) {
			this.lastOutput = true;
			checkRelease();
		}
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception {
		if(isContinue(msg)) {
			ctx.channel().writeAndFlush(content==null?LastHttpContent.EMPTY_LAST_CONTENT:new DefaultLastHttpContent(content));
			return;
		} 
		
		if(!isContinue(msg) && msg instanceof LastHttpContent) {
			lastInput();
		}
		channelRead1(ctx,msg);
	}
	
	private boolean isContinue(T msg) {
		return msg instanceof HttpResponse && ((HttpResponse) msg).status().equals(HttpResponseStatus.CONTINUE);
	}

	protected void lastInput() {
		this.lastInput = true;
		checkRelease();
	}

	private void checkRelease() {
		if(this.lastOutput && this.lastInput) {
			channel.pipeline().remove(this);
			channel.release();
		}
	}

	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	protected abstract void channelRead1(ChannelHandlerContext ctx, T msg) throws Exception;
}
