package com.simplyti.service.channel;

import io.netty.channel.ChannelPipeline;

public interface EntryChannelInit {

	void init(ChannelPipeline pipeline);

}
