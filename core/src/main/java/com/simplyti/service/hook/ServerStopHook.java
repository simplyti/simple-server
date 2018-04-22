package com.simplyti.service.hook;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

public interface ServerStopHook {
	
	Future<Void> executeStop(EventLoop startStopLoop);

}
