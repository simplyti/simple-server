package com.simplyti.service.clients.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class MultiplexStreamlHandler extends ChannelInboundHandlerAdapter {
	
	private final Map<Long,EmbeddedChannel> subPipelines = new HashMap<>();

	public MultiplexStreamlHandler(MultiplexedClientChannel clientChannel) {
		subPipelines.put(clientChannel.streamId(), clientChannel);
	}
	
	public void newStream(MultiplexedClientChannel clientChannel) {
		subPipelines.put(clientChannel.streamId(), clientChannel);
	}

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof MultiplexStreamResponse) {
			long id = ((MultiplexStreamResponse) msg).id();
			EmbeddedChannel channel = subPipelines.get(id);
			if(channel!= null) {
				channel.writeInbound(msg);
			} else {
				ctx.fireChannelRead(msg);
			}
		} else {
			ctx.fireChannelRead(msg);
		}
    }
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		subPipelines.values().forEach(ch->ch.close());
    }

}
