package com.simplyti.util.concurrent;

import java.util.concurrent.CompletionStage;

public interface Future<T> extends io.netty.util.concurrent.Future<T>, CompletionStage<T>{

}
