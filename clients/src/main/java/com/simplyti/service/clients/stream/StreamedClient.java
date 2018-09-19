package com.simplyti.service.clients.stream;

import com.simplyti.service.clients.ClientRequestChannel;
import com.simplyti.service.clients.pending.PendingMessages;

import io.netty.channel.EventLoop;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class StreamedClient<T> {

	private final Future<ClientRequestChannel<Void>> clientChannel;
	private final PendingMessages pendingMessages;
	private final EventLoop executor;
	private boolean pendingDone= false;
	
	public StreamedClient(Future<ClientRequestChannel<Void>> clientChannel, EventLoop executor) {
		this.clientChannel=clientChannel;
		this.pendingMessages=new PendingMessages();
		this.executor=executor;
		clientChannel.addListener(f->{
			if(executor.inEventLoop()) {
				handleConnection();
			} else {
				executor.submit(this::handleConnection);
			}
		});
	}

	private void handleConnection() {
		if(clientChannel.isSuccess()) {
			pendingMessages.write(clientChannel.getNow()).addListener(f->pendingDone=true);
		}else {
			pendingMessages.fail(clientChannel.cause());
			pendingDone=true;
		}
	}

	public Future<Void> send(T msg) {
		if (clientChannel.isDone()) {
			if (clientChannel.isSuccess()) {
				if(pendingDone) {
					Promise<Void> promise = executor.newPromise();
					clientChannel.getNow().writeAndFlush(msg).addListener(f->toPromise(f,promise));
					return promise;
				}else {
					return addPending(msg);
				}
			}else {
				ReferenceCountUtil.release(msg);
				return executor.newFailedFuture(clientChannel.cause());
			}
		}else {
			return addPending(msg);
		}
	}

	private Promise<Void> addPending(Object msg) {
		Promise<Void> promise = executor.newPromise();
		if(executor.inEventLoop()) {
			addPending0(promise,msg);
		}else {
			executor.submit(()->addPending0(promise,ReferenceCountUtil.retain(msg)));
		}
		return promise;
	}
	
	private void addPending0(Promise<Void> promise, Object msg) {
		if(pendingDone) {
			clientChannel.getNow().writeAndFlush(msg).addListener(f->toPromise(f,promise));
		}else {
			pendingMessages.pending(promise,msg);
		}
	}

	private void toPromise(Future<? super Void> f, Promise<Void> promise) {
		if(f.isSuccess()) {
			promise.setSuccess(null);
		}else {
			promise.setFailure(f.cause());
		}
	}

	public boolean isDone() {
		return clientChannel.isDone() && (!clientChannel.isSuccess() || (clientChannel.isSuccess() && clientChannel.getNow().isDone()));
	}

	public boolean isSuccess() {
		return clientChannel.isSuccess() && clientChannel.getNow().isSuccess();
	}
}
