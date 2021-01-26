package com.simplyti.service.clients.http.request;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;


public class DefaultChunckedBodyRequest implements ChunckedBodyRequest {

	private final ClientChannel channel;

	public DefaultChunckedBodyRequest(ClientChannel channel) {
		this.channel=channel;
	}

	@Override
	public Future<Void> send(String data) {
		ByteBuf content = this.channel.alloc().buffer();
		content.writeCharSequence(data, CharsetUtil.UTF_8);
		HttpContent chunk = new DefaultHttpContent(content);
		return new DefaultFuture<>(channel.writeAndFlush(chunk),channel.eventLoop());
	}

	@Override
	public Future<Void> end() {
		return new DefaultFuture<>(this.channel.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT),this.channel.eventLoop());
	}
	
}
