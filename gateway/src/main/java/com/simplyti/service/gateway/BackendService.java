package com.simplyti.service.gateway;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.MoreObjects;
import com.simplyti.service.api.builder.PathPattern;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.gateway.balancer.RoundRobinLoadBalancer;
import com.simplyti.service.gateway.balancer.ServiceBalancer;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@AllArgsConstructor
@EqualsAndHashCode(of= {"host","method","path"})
@ToString(of= {"host","method","path", "loadBalander"})
public class BackendService implements Comparable<BackendService>{
	
	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	private final String host;
	private final HttpMethod method;
	private final String path;
	private final Rewrite rewrite;
	private final Set<HttpRequestFilter> filters;
	private final Pattern pattern;
	private final PathPattern pathPattern;
	private final int literalCount;
	private final boolean tlsEnabled;
	
	private ServiceBalancer loadBalander;

	public BackendService(String host, HttpMethod method, String path, String rewrite, boolean tlsEnabled, Set<HttpRequestFilter> filters, Collection<Endpoint> endpoints) {
		this.loadBalander = new RoundRobinLoadBalancer(endpoints);
		this.host=host;
		this.method=method;
		this.path=path;
		this.rewrite=rewrite==null?null:new Rewrite(rewrite);
		this.tlsEnabled=tlsEnabled;
		this.filters=MoreObjects.firstNonNull(filters, Collections.emptySet());
		if(path==null) {
			this.pathPattern=null;
			this.pattern = null;
			literalCount=0;
		}else {
			PathPattern thePattern = PathPattern.build(path);
			if(thePattern.pathParamNameToGroup().isEmpty()) {
				this.pathPattern = null;
				this.pattern = Pattern.compile(path.replaceAll("/+$",  StringUtil.EMPTY_STRING)+"/?(.*)");
				this.literalCount = thePattern.literalCount();
			}else {
				this.pathPattern = thePattern;
				this.pattern = thePattern.pattern();
				this.literalCount = thePattern.literalCount();
			}
		}
	}

	@Override
	public int compareTo(BackendService other) {
		int compare = compareHost(other);
		if(compare==0) {
			compare = compareMethod(other);
			if(compare ==0) {
				return comparePath(other);
			}else{
				return compare;
			}
		}else{
			return compare;
		}
	}

	private int compareMethod(BackendService other) {
		if(method() == null) {
			if(other.method() == null) {
				return 0;
			}else {
				return 1;
			}
		}else {
			if(other.method() == null) {
				return -1;
			}else {
				return method().compareTo(other.method());
			}
		}
	}

	private int compareHost(BackendService other) {
		if(host() == null) {
			if(other.host() == null) {
				return 0;
			}else {
				return 1;
			}
		}else {
			if(other.host() == null) {
				return -1;
			}else {
				return host().compareTo(other.host());
			}
		}
	}

	private int comparePath(BackendService other) {
		if(path() == null) {
			if(other.path() == null) {
				return 0;
			}else {
				return 1;
			}
		}else {
			if(other.path() == null) {
				return -1;
			}else {
				int literalParts = other.literalCount()-literalCount();
				if(literalParts==0) {
					return this.path().compareTo(other.path());
				}else {
					return literalParts;
				}
			}
		}
	}

	public void add(Endpoint endpoint) {
		this.loadBalander = loadBalander.add(endpoint);
		log.info("Added service endpoint: {}",endpoint);
	}
	
	public void delete(Endpoint endpoint) {
		this.loadBalander = loadBalander.delete(endpoint);
		log.info("Deleted service endpoint: {}",endpoint);
	}

	public void clear() {
		this.loadBalander = loadBalander.clear();
		log.info("Cleared service endpoints");
	}

	public Set<HttpRequestFilter> filters() {
		return filters;
	}

	public boolean tlsEnabled() {
		return tlsEnabled;
	}

}
