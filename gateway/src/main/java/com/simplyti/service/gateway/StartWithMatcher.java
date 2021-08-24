package com.simplyti.service.gateway;

import com.simplyti.service.matcher.ApiMatcher;

public class StartWithMatcher implements ApiMatcher {

	private final boolean matches;
	private final String value;

	public StartWithMatcher(String path, String value) {
		this.matches=value.startsWith(path);
		this.value=value;
	}

	@Override
	public boolean matches() {
		return matches;
	}

	@Override
	public String group(int group) {
		if(group==0) {
			return value;
		} else {
			return null;
		}
	}

}
