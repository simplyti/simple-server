package com.simplyti.service.gateway;

import java.util.Map.Entry;

import com.simplyti.service.matcher.ApiMatcher;
import com.simplyti.service.matcher.ApiPattern;

import io.netty.util.internal.StringUtil;

public class Rewrite {

	private final String rewrite;
	private final ApiPattern pathPattern;

	public Rewrite(String rewrite) {
		this.rewrite=rewrite;
		this.pathPattern = ApiPattern.build(rewrite);
	}

	public String doRewrite(ApiMatcher matcher, ApiPattern servicePattern) {
		if(servicePattern!=null && !pathPattern.pathParamNameToGroup().isEmpty()) {
			String rewrited = new String(rewrite);
			for(Entry<String,Integer> entry:pathPattern.pathParamNameToGroup().entrySet()) {
				String value = matcher.group(entry.getValue());
				rewrited = rewrited.replaceAll("\\{"+entry.getKey()+"\\}", value);
			}
			return rewrited;
		}else {
			String group =matcher.group(1);
			return rewrite.replaceAll("/$", StringUtil.EMPTY_STRING)+(group.isEmpty()?group:"/"+group);
		}
	}

	public String rewrite() {
		return rewrite;
	}

}
