package com.simplyti.service.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;

import javax.security.cert.X509Certificate;

import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ServerCertificateHandler extends ChannelInboundHandlerAdapter {
	
	private X509Certificate lastCert;

	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof SslHandshakeCompletionEvent) {
			SslHandler ssl = ctx.pipeline().get(SslHandler.class);
			this.lastCert = ssl.engine().getSession().getPeerCertificateChain()[0];
		}
        ctx.fireUserEventTriggered(evt);
    }

	public X509Certificate lastCertificate() {
		return lastCert;
	}

}
