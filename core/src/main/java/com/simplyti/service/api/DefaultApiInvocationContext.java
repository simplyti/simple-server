package com.simplyti.service.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.jsoniter.JsonIterator;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sse.DefaultSSEStream;
import com.simplyti.service.sse.SSEStream;
import com.simplyti.service.sse.ServerSentEventEncoder;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;

import static io.vavr.control.Try.run;

public class DefaultApiInvocationContext<I,O>  extends DefaultByteBufHolder implements ApiInvocationContext<I,O>, Supplier<I>{
	
	private final ExceptionHandler exceptionHandler;
	private final ChannelHandlerContext ctx;
	private final ApiInvocation<I> msg;
	private final Supplier<I> cachedRequestBody;
	private final ServerSentEventEncoder serverSentEventEncoder;
	private final SyncTaskSubmitter syncTaskSubmitter;
	
	private boolean released = false ;
	
	
	public DefaultApiInvocationContext(ChannelHandlerContext ctx,ApiInvocation<I> msg, ExceptionHandler exceptionHandler,
			ServerSentEventEncoder serverSentEventEncoder, SyncTaskSubmitter syncTaskSubmitter) {
		super(msg.content());
		this.exceptionHandler=exceptionHandler;
		this.ctx=ctx;
		this.msg=msg;
		this.cachedRequestBody=Suppliers.memoize(this);
		this.serverSentEventEncoder=serverSentEventEncoder;
		this.syncTaskSubmitter=syncTaskSubmitter;
	}
	
	@Override
	public String pathParam(String key) {
		return msg.pathParam(key);
	}

	@Override
	public EventExecutor executor() {
		return ctx.executor();
	}

	@Override
	public I body() {
		return cachedRequestBody.get();
	}
	
	@Override
	public Future<Void> send(O response) {
		tryRelease();
		return ctx.writeAndFlush(new ApiResponse(response,msg.isKeepAlive()))
			.addListener(this::writeListener);
	}
	
	private void release0() {
		release();
		released=true;
	}

	public void writeListener(Future<? super Void> f) {
		if(f.isSuccess()) {
			if(!msg.isKeepAlive()) {
				ctx.channel().close();
			}
			tryRelease();
		}else {
			failure(f.cause());
		}
	}
	
	@SuppressWarnings("unchecked")
	public Future<Void> send(FullHttpResponse response) {
		if(response==null) {
			return send((O) response);
		}
		return ctx.writeAndFlush(response)
			.addListener(this::writeListener);
	}
	
	public void tryRelease() {
		if(!released) {
			release0();
		}
	}

	@Override
	public Future<Void> failure(Throwable error) {
		tryRelease();
		return exceptionHandler.exceptionCaught(ctx,error);
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
		run(decoder::destroy);
		return (I) files;
	}

	@Override
	public String queryParam(String name) {
		if(this.msg.params().containsKey(name)) {
			return Iterables.getFirst(this.msg.params().get(name), null);
		}else {
			return null;
		}
	}

	@Override
	public List<String> queryParams(String name) {
		return this.msg.params().get(name);
	}
	
	@Override
	public Map<String,List<String>> queryParams() {
		return this.msg.params();
	}

	@Override
	public Future<Void> close() {
		release();
		return ctx.close();
	}
	
	@Override
	public Channel channel() {
		return ctx.channel();
	}

	@Override
	public HttpRequest request() {
		return msg.request();
	}

	@Override
	public SSEStream sse() {
		tryRelease();
		return new DefaultSSEStream(ctx,serverSentEventEncoder);
	}

	@Override
	public Future<O> sync(Callable<O> task) {
		return syncTaskSubmitter.submit(ctx.executor(), task);
	}

}
