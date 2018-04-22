package com.simplyti.service.channel.handler;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import com.simplyti.service.api.ApiInvocation;
import com.simplyti.service.api.ApiMacher;
import com.simplyti.service.api.ApiResolver;
import com.simplyti.service.exception.NotFoundException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.RequiredArgsConstructor;

@Sharable
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ApiInvocationDecoder extends MessageToMessageDecoder<FullHttpRequest>{
	
	private final ApiResolver managerApiResolver;
	
	private final Optional<DefaultBackendHandler> defaultBackendHandler;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void decode(ChannelHandlerContext ctx, FullHttpRequest msg, List<Object> out) throws Exception {
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(msg.uri());
		Optional<ApiMacher> operation = managerApiResolver.getOperationFor(msg.method(),queryStringDecoder.path());
		if (operation.isPresent()) {
			out.add(new ApiInvocation(operation.get().operation(),operation.get().matcher(),queryStringDecoder.parameters(),msg));
		}else {
			if(defaultBackendHandler.isPresent()) {
				defaultBackendHandler.get().handle(ctx,msg);
			}else {
				ctx.fireExceptionCaught(new NotFoundException());
			}
		}
	}

}
