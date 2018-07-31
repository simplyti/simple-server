package com.simplyti.service.clients.http.sse;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

public class ServerSentEventDecoder extends MessageToMessageDecoder<ByteBuf>  {

	private String event;
	private String id;
	private String data;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		if(!msg.isReadable()) {
			out.add(new ServerEvent(event,id,data));
			event=null;
			id=null;
			data=null;
		}else {
			int index = msg.forEachByte(b->b!=':');
			CharSequence field = msg.readCharSequence(index-msg.readerIndex(), CharsetUtil.UTF_8);
			if(field.equals("data")) {
				if(data!=null) {
					data+="\n"+readValue(msg);
				}else {
					data=readValue(msg);
				}
			}else if(field.equals("id")) {
				id=readValue(msg);
			}else if(field.equals("event")) {
				event=readValue(msg);
			}else {
				msg.skipBytes(msg.readableBytes());
			}
		}
	}

	private String readValue(ByteBuf msg) {
		return msg.skipBytes(1).readCharSequence(msg.readableBytes(), CharsetUtil.UTF_8).toString().replaceAll("^\\s*", "");
	}



}
