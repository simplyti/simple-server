package com.simplyti.service.channel.handler.inits;

import io.netty.channel.ChannelPipeline;

public interface ServiceHadlerInit {

	void init(ChannelPipeline pipeline);

}
