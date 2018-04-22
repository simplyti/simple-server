package com.simplyti.service.api;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.jsoniter.JsonIterator;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;

import static io.vavr.control.Try.run;

public class DefaultApiInvocationContext<I,O>  extends DefaultByteBufHolder implements ApiInvocationContext<I,O>, Supplier<I>{
	
	private final ChannelHandlerContext ctx;
	private final ApiInvocation<I> msg;
	
	private final Map<String,String> cachedpathParams = Maps.newHashMap();
	private final Supplier<I> cachedRequestBody;
	
	private boolean released = false ;
	
	public DefaultApiInvocationContext(ChannelHandlerContext ctx,ApiInvocation<I> msg) {
		super(msg.content());
		this.ctx=ctx;
		this.msg=msg;
		this.cachedRequestBody=Suppliers.memoize(this);
	}
	
	public String uri() {
		return msg.uri();
	}

	public String pathParam(String key) {
		return cachedpathParams.computeIfAbsent(key, theKey->{
			Integer group = msg.operation().pathParamNameToGroup().get(key);
			if(group==null){
				return null;
			}else{
				return msg.matcher().group(group);
			}
		});
	}

	public EventExecutor executor() {
		return ctx.executor();
	}

	public I body() {
		return cachedRequestBody.get();
	}
	
	@Override
	public Future<Void> send(O response) {
		return ctx.writeAndFlush(new ApiResponse(response,msg.isKeepAlive()))
			.addListener(this::writeListener);
	}
	
	public void writeListener(Future<? super Void> f) {
		if(!msg.isKeepAlive()) {
			ctx.channel().close();
		}
		tryRelease();
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
			release();
		}
	}

	public void failure(Throwable error) {
		tryRelease();
		ctx.fireExceptionCaught(error);
	}

	@SuppressWarnings("unchecked")
	@Override
	public I get() {
		final I result;
		if(msg.operation().requestType().getType().equals(ByteBuf.class)){
			result =  (I) content();
		} else if(!content().isReadable()){
			result = null;
			release();
			released=true;
		} else if(msg.operation().requestType().getType().equals(Void.class)){
			result = null;
			release();
			released=true;
		} else if(msg.operation().requestType().getType().equals(String.class)){
			result = (I) content().toString(CharsetUtil.UTF_8);
			release();
			released=true;
		} else if(msg.operation().isMultipart()){
			result = decodeMultipart();
		} else{
			byte[] data = new byte[content().readableBytes()];
			content().readBytes(data);
			result = JsonIterator.deserialize(data).as(msg.operation().requestType());
			release();
			released=true;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private I decodeMultipart() {
		HttpPostMultipartRequestDecoder decoder = new HttpPostMultipartRequestDecoder(msg.request());
		List<com.simplyti.service.api.multipart.FileUpload> files = decoder.getBodyHttpDatas().stream()
			.filter(data->data.getHttpDataType().equals(HttpDataType.FileUpload))
			.map(data->FileUpload.class.cast(data))
			.map(data->new com.simplyti.service.api.multipart.FileUpload(data.content().retain(),data.getFilename()))
			.collect(Collectors.toList());
		run(decoder::destroy);
		return (I) files;
	}

	public String queryParam(String name) {
		if(this.msg.params().containsKey(name)) {
			return Iterables.getFirst(this.msg.params().get(name), null);
		}else {
			return null;
		}
	}

	public List<String> queryParams(String name) {
		return this.msg.params().get(name);
	}

	public Future<Void> close() {
		release();
		return ctx.close();
	}

	@Override
	public HttpHeaders headers() {
		return msg.headers();
	}

	@Override
	public HttpRequest request() {
		return msg.request();
	}

	@Override
	public ChannelHandlerContext channelContext() {
		return ctx;
	}

}
