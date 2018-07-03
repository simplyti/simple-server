package com.simplyti.service.gateway;

import javax.inject.Inject;

import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.exception.NotFoundException;
import com.simplyti.service.exception.ServiceException;
import com.simplyti.service.gateway.balancer.ServiceBalancer;
import com.simplyti.service.gateway.handler.BackendProxyHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class GatewayRequestHandler extends DefaultBackendRequestHandler {
	
	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	private final ServiceDiscovery serviceDiscovery;
	private final InternalClient client;

	private Channel backendChannel;

	private PendingMessage head;
	private PendingMessage tail;
	private boolean failPrematurely;

	@Inject
	public GatewayRequestHandler(InternalClient client, ServiceDiscovery serviceDiscovery) {
		this.client = client;
		this.serviceDiscovery = serviceDiscovery;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) msg;
			QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
			ServiceBalancer service = serviceDiscovery.get(request.headers().get(HttpHeaderNames.HOST),request.method(), decoder.path());
			if (service == null) {
				ctx.fireExceptionCaught(new NotFoundException());
			} else {
				Endpoint endpoint = service.next();
				if (endpoint == null) {
					ctx.fireExceptionCaught(new ServiceException(HttpResponseStatus.SERVICE_UNAVAILABLE));
				} else {
					connectToEndpoint(ctx, endpoint);
				}
			}
		}
		
		if (backendChannel != null) {
			writePendingWrites();
			backendChannel.writeAndFlush(ReferenceCountUtil.retain(msg)).addListener(f->handleWriteFuture(ctx,f));
		} else if(!failPrematurely){
			addPendingWrite(msg);
		}
	}
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().config().setAutoRead(false);
    }
	
	@Override
	 public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().config().setAutoRead(true);
    }

	private void handleWriteFuture(ChannelHandlerContext ctx,Future<?> f) {
		if(f.isSuccess()) {
			ctx.channel().read();
		}else {
			ctx.channel().close();
		}
	}

	private void addPendingWrite(Object msg) {
		PendingMessage message = PendingMessage.newInstance(ReferenceCountUtil.retain(msg));
		PendingMessage currentTail = tail;
		if (currentTail == null) {
			tail = head = message;
		} else {
			currentTail.next = message;
			tail = message;
		}
	}

	private Future<Void> writePendingWrites() {
		if (isEmpty()) {
			return backendChannel.eventLoop().newSucceededFuture(null);
		}
		
		Promise<Void> promise = backendChannel.eventLoop().newPromise();
		PromiseCombiner combiner = new PromiseCombiner();
		PendingMessage write = head;
		head = tail = null;
		while (write != null) {
			PendingMessage next = write.next;
			Object msg = write.msg;
			write.recycle();
			combiner.add(backendChannel.writeAndFlush(msg));
			write = next;
		}
		combiner.finish(promise);
		return promise;
	}

	private void failPendingWrites() {
		if (isEmpty()) {
			return;
		}
		
		PendingMessage write = head;
		head = tail = null;
		while (write != null) {
			PendingMessage next = write.next;
			ReferenceCountUtil.release(write.msg);
			write.recycle();
			write = next;
		}
	}

	public boolean isEmpty() {
		return head == null;
	}

	private void connectToEndpoint(ChannelHandlerContext ctx, Endpoint endpoint) {
		ChannelPool pool = client.pool(endpoint);
		Future<Channel> channelFuture = pool.acquire();
		if (channelFuture.isDone()) {
			handleBackendChannelFuture(ctx, channelFuture, pool, endpoint);
		} else {
			channelFuture.addListener(f -> handleBackendChannelFuture(ctx, channelFuture, pool, endpoint));
		}
	}

	private void handleBackendChannelFuture(ChannelHandlerContext ctx, Future<Channel> backendChannelFuture,
			ChannelPool pool, Endpoint endpoint) {
		if (backendChannelFuture.isSuccess()) {
			this.backendChannel = backendChannelFuture.getNow();
			backendChannel.pipeline().addLast(new BackendProxyHandler(pool, ctx.channel()));
			if(ctx.executor().inEventLoop()) {
				writePendingWrites().addListener(f->handleWriteFuture(ctx,f));
			}else {
				ctx.executor().submit(()->writePendingWrites().addListener(f->handleWriteFuture(ctx,f)));
			}
		} else {
			failPrematurely=true;
			log.warn("Cannot connect to backend {}: {}", endpoint, backendChannelFuture.cause().toString());
			ctx.fireExceptionCaught(new ServiceException(HttpResponseStatus.BAD_GATEWAY));
			if(ctx.executor().inEventLoop()) {
				failPendingWrites();
			}else {
				ctx.executor().submit(this::failPendingWrites);
			}
		}
	}

	static final class PendingMessage {
		private static final Recycler<PendingMessage> RECYCLER = new Recycler<PendingMessage>() {
			@Override
			protected PendingMessage newObject(Handle<PendingMessage> handle) {
				return new PendingMessage(handle);
			}
		};

		private final Recycler.Handle<PendingMessage> handle;
		private Object msg;
		private PendingMessage next;

		private PendingMessage(Recycler.Handle<PendingMessage> handle) {
			this.handle = handle;
		}

		static PendingMessage newInstance(Object msg) {
			PendingMessage message = RECYCLER.get();
			message.msg = msg;
			return message;
		}

		private void recycle() {
			msg = null;
			next = null;
			handle.recycle(this);
		}
	}

}
