package com.simplyti.service.hook;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

public interface ServerStartHook {

	Future<Void> executeStart(EventLoop startStopLoop);

}
