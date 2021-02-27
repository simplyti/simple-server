package com.simplyti.service.clients.http.handler;

import java.util.List;

import com.simplyti.service.clients.http.sse.domain.ServerEvent;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

public class HttpServerSentEventDecoder extends MessageToMessageDecoder<ByteBuf> {
	
	private static final String DATA = "data";
	private static final String EVENT = "event";
	private static final String ID = "id";

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf frame, List<Object> out) throws Exception {
		String event = null;
		String id = null;
		String data = null;
		
		while(frame.isReadable()) {
			int endIndex = frame.forEachByte(b->b!='\n');
			ByteBuf line;
			if(endIndex>0) {
				line = frame.readSlice(endIndex-frame.readerIndex());
				frame.skipBytes(1);
			} else {
				line= frame;
			}
			int index = line.forEachByte(b->b!=':');
			CharSequence field = line.readCharSequence(index-line.readerIndex(), CharsetUtil.UTF_8);
			if(field.equals(DATA)) {
				if(data!=null) {
					data+="\n"+readValue(line);
				}else {
					data=readValue(line);
				}
			}else if(field.equals(ID)) {
				id=readValue(line);
			}else if(field.equals(EVENT)) {
				event=readValue(line);
			}else {
				line.skipBytes(line.readableBytes());
			}
		}
		out.add(new ServerEvent(event,id,data));
	}
	
	private String readValue(ByteBuf msg) {
		return msg.skipBytes(1).readCharSequence(msg.readableBytes(), CharsetUtil.UTF_8).toString().replaceAll("^\\s*", "");
	}



}
