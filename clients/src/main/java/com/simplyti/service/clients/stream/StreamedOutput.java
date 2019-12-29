package com.simplyti.service.clients.stream;


import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.commons.netty.pending.PendingMessages;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;

public class StreamedOutput {
	
	private final Future<ClientChannel> clientChannel;
	private final PendingMessages pendingMessages;
	private final EventExecutor executor;
	
	private boolean pendingDone= false;
	
	public StreamedOutput(Future<ClientChannel> clientChannel,EventExecutor executor) {
		this.clientChannel=clientChannel;
		this.pendingMessages=new PendingMessages();
		this.executor=executor;
		this.clientChannel.addListener(f->{
			if(executor.inEventLoop()) {
				handleConnection();
			} else {
				executor.submit(this::handleConnection);
			}
		});
	}
	
	private void handleConnection() {
		if(clientChannel.isSuccess()) {
			pendingMessages.write(clientChannel.getNow()).addListener(f->{
				if(executor.inEventLoop()) {
					handlePendingWrite();
				} else {
					executor.submit(this::handlePendingWrite);
				}
			});
		}else {
			pendingMessages.fail(clientChannel.cause());
			pendingDone=true;
		}
	}
	
	private void handlePendingWrite() {
		pendingDone=true;
		pendingMessages.write(clientChannel.getNow());
	}

	public Future<Void> send(Object msg) {
		if (clientChannel.isDone()) {
			if (clientChannel.isSuccess()) {
				if(pendingDone) {
					Promise<Void> promise = executor.newPromise();
					clientChannel.getNow().writeAndFlush(msg).addListener(f->toPromise(f,promise));
					return new DefaultFuture<>(promise,executor);
				}else {
					return addPending(msg);
				}
			}else {
				ReferenceCountUtil.release(msg);
				return new DefaultFuture<>(executor.newFailedFuture(clientChannel.cause()),executor);
			}
		}else {
			return addPending(msg);
		}
	}
	
	private Future<Void> addPending(Object msg) {
		Promise<Void> promise = executor.newPromise();
		if(executor.inEventLoop()) {
			addPending0(promise,msg);
		}else {
			executor.submit(()->addPending0(promise,ReferenceCountUtil.retain(msg)));
		}
		return new DefaultFuture<>(promise,executor);
	}
	
	private void addPending0(Promise<Void> promise, Object msg) {
		if(pendingDone) {
			clientChannel.getNow().writeAndFlush(msg).addListener(f->toPromise(f,promise));
		}else {
			pendingMessages.pending(promise,ReferenceCountUtil.retain(msg));
		}
	}
	
	private static void toPromise(io.netty.util.concurrent.Future<? super Void> f, Promise<Void> promise) {
		if(f.isSuccess()) {
			promise.setSuccess(null);
		}else {
			promise.setFailure(f.cause());
		}
	}

}
