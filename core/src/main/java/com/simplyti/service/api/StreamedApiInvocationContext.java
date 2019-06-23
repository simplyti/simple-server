package com.simplyti.service.api;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Future;

public interface StreamedApiInvocationContext<T> extends APIContext<T>{

	public Future<Void> stream(Consumer<ByteBuf> object);
	
	public List<String> queryParams(String name);
	public Map<String,List<String>> queryParams();
	public String queryParam(String name);
	public String pathParam(String key);
	
}
