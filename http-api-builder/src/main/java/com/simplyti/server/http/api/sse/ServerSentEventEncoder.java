package com.simplyti.server.http.api.sse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

@Sharable
public class ServerSentEventEncoder extends MessageToByteEncoder<ServerEvent>{

	@Override
	protected void encode(ChannelHandlerContext ctx, ServerEvent msg, ByteBuf out) throws Exception {
		if(msg.id()!=null) {
			out.writeCharSequence("id: ", CharsetUtil.UTF_8);
			out.writeCharSequence(msg.id(), CharsetUtil.UTF_8);
			out.writeCharSequence("\n", CharsetUtil.UTF_8);
		}
		if(msg.event()!=null) {
			out.writeCharSequence("event: ", CharsetUtil.UTF_8);
			out.writeCharSequence(msg.event(), CharsetUtil.UTF_8);
			out.writeCharSequence("\n", CharsetUtil.UTF_8);
		}
		if(msg.data()!=null) {
			out.writeCharSequence("data: ", CharsetUtil.UTF_8);
			out.writeCharSequence(msg.data(), CharsetUtil.UTF_8);
			out.writeCharSequence("\n", CharsetUtil.UTF_8);
		}
		
		out.writeCharSequence("\n", CharsetUtil.UTF_8);
	}

}
