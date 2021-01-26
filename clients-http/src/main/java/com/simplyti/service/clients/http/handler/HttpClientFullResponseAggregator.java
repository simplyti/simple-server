package com.simplyti.service.clients.http.handler;

import java.util.List;

import com.simplyti.service.clients.http.HttpClientStreamEvent;
import com.simplyti.service.clients.http.exception.HttpException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;

public class HttpClientFullResponseAggregator extends HttpObjectAggregator {

	private boolean streamed;
	private boolean checkStatus;
	private boolean error;

	public HttpClientFullResponseAggregator(int maxContentLength) {
		super(maxContentLength);
	}
	
	protected void decode(final ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
		if(streamed) {
			if(checkStatus && msg instanceof HttpResponse && isError(((HttpResponse) msg).status().codeClass())) {
				this.error = true;
				throw new HttpException(((HttpResponse) msg).status().code());
			} else if(error) {
				if(msg instanceof LastHttpContent) {
					ctx.fireChannelRead(LastHttpContent.EMPTY_LAST_CONTENT);
				}
				return;
			} 
			ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
		} else {
			super.decode(ctx, msg, out);
		}
	}
	
	private boolean isError(HttpStatusClass codeClass) {
		return codeClass.equals(HttpStatusClass.CLIENT_ERROR) || 
				codeClass.equals(HttpStatusClass.SERVER_ERROR);
	}
	
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof HttpClientStreamEvent) {
			HttpClientStreamEvent http = (HttpClientStreamEvent) evt;
			if(http.type() == HttpClientStreamEvent.Type.START) {
				this.streamed=true;
				this.checkStatus = http.checkStatus();
			} else if(http.type() == HttpClientStreamEvent.Type.STOP) {
				this.streamed=false;
				this.checkStatus=false;
				this.error=false;
			}
		}
		ctx.fireUserEventTriggered(evt);
    }


}
