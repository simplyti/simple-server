package com.simplyti.service.clients.http.request;

import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface FinishableBodyHttpRequest extends FinishableHttpRequest{

	FinishableHttpRequest body(Function<ByteBufAllocator, ByteBuf> bodySupplier);

}
