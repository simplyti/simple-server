package com.simplyti.service.gateway;

import java.util.Map.Entry;
import java.util.regex.Matcher;

import com.simplyti.service.api.builder.PathPattern;

import io.netty.util.internal.StringUtil;

public class Rewrite {

	private final String rewrite;
	private final PathPattern pathPattern;

	public Rewrite(String rewrite) {
		this.rewrite=rewrite;
		this.pathPattern = PathPattern.build(rewrite);
	}

	public String doRewrite(Matcher matcher, PathPattern servicePattern) {
		if(servicePattern!=null && !pathPattern.pathParamNameToGroup().isEmpty()) {
			String rewrited = new String(rewrite);
			for(Entry<String,Integer> entry:pathPattern.pathParamNameToGroup().entrySet()) {
				String value = matcher.group(entry.getValue());
				rewrited = rewrited.replaceAll("\\{"+entry.getKey()+"\\}", value);
			}
			return rewrited;
		}else {
			return rewrite.replaceAll("/$", StringUtil.EMPTY_STRING)+"/"+matcher.group(1);
		}
	}

	public String rewrite() {
		return rewrite;
	}

}
