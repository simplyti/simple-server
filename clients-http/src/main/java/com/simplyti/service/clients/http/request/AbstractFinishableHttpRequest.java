package com.simplyti.service.clients.http.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

import com.simplyti.service.clients.ClientConfig;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.http.handler.DecodingFullHttpResponseHandler;
import com.simplyti.service.clients.http.handler.FullHttpResponseHandler;
import com.simplyti.service.clients.http.handler.HttpResponseHandler;
import com.simplyti.service.clients.http.handler.ServerEventResponseHandler;
import com.simplyti.service.clients.http.handler.StreamResponseHandler;
import com.simplyti.service.clients.http.sse.ServerEvent;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.concurrent.Future;

public abstract class AbstractFinishableHttpRequest implements FinishableHttpRequest {
	
	protected final InternalClient client;
	private final boolean checkStatusCode;
	private final ClientConfig config;
	private final String charsetName;
	
	private final Map<String,Object> params;
	
	public AbstractFinishableHttpRequest(InternalClient client, boolean checkStatusCode, ClientConfig config) {
		this.client = client;
		this.config=config;
		this.checkStatusCode=checkStatusCode;
		this.params=new HashMap<>();
		this.charsetName=HttpConstants.DEFAULT_CHARSET.name();
	}
	
	@Override
	public FinishableHttpRequest params(Map<String, String> params) {
		this.params.putAll(params);
		return this;
	}
	
	@Override
	public FinishableHttpRequest param(String name) {
		this.params.put(name, null);
		return this;
	}
	
	@Override
	public FinishableHttpRequest param(String name, Object value) {
		this.params.put(name, value);
		return this;
	}
	
	@Override
	public Future<FullHttpResponse> fullResponse() {
		return client.channel(channel->{
			channel.pipeline().addLast(new FullHttpResponseHandler<>(channel,checkStatusCode));
		},request(),config);
	}
	
	@Override
	public <T> Future<T> fullResponse(Function<FullHttpResponse, T> function) {
		return client.channel(channel->{
			channel.pipeline().addLast(new DecodingFullHttpResponseHandler<>(function,channel,checkStatusCode));
		},request(),config);
	}

	@Override
	public Future<Void> forEach(Consumer<HttpObject> consumer) {
		return client.channel(channel->{
			channel.pipeline().addLast(new HttpResponseHandler(channel,consumer));
		},request(),config);
	}

	@Override
	public Future<Void> stream(Consumer<ByteBuf> consumer) {
		return client.channel(channel->{
			channel.pipeline().addLast(new StreamResponseHandler(channel,consumer));
		},request(),config);
	}
	
	@Override
	public Future<Void> sse(Consumer<ServerEvent> consumer) {
		return client.channel(channel->{
			channel.pipeline().addLast(new ServerEventResponseHandler(channel,consumer));
		},request(),config);
	}
	
	private FullHttpRequest request() {
		if(params.isEmpty()) {
			return request0();
		}
		
		FullHttpRequest request = request0();
		final StringBuilder uriBuilder = new StringBuilder(request.uri());
		boolean hasParams = false;
		for(Entry<String,Object> entry:params.entrySet()) {
			if (hasParams) {
	            uriBuilder.append('&');
	        } else {
	            uriBuilder.append('?');
	            hasParams = true;
	        }
			appendComponent(entry.getKey(), charsetName, uriBuilder,false);
			if (entry.getValue() != null) {
	            uriBuilder.append('=');
	            appendComponent(entry.getValue().toString(), charsetName, uriBuilder,true);
	        }
		}
		
		return request.setUri(uriBuilder.toString());
	}
	
	private static void appendComponent(String s, String charset, StringBuilder sb, boolean urlEncode) {
		if(urlEncode) {
			try {
	            s = URLEncoder.encode(s, charset);
	        } catch (UnsupportedEncodingException ignored) {
	            throw new UnsupportedCharsetException(charset);
	        }
		}
        // replace all '+' with "%20"
        int idx = s.indexOf('+');
        if (idx == -1) {
            sb.append(s);
            return;
        }
        sb.append(s, 0, idx).append("%20");
        int size = s.length();
        idx++;
        for (; idx < size; idx++) {
            char c = s.charAt(idx);
            if (c != '+') {
                sb.append(c);
            } else {
                sb.append("%20");
            }
        }
    }
	
	protected abstract FullHttpRequest request0();

}
