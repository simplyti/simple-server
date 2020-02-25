package com.simplyti.server.http.api.pattern;

public class EqualsApiMatcher implements ApiMatcher {

	private final boolean matches;
	private final String value;

	public EqualsApiMatcher(String path, String value) {
		this.matches=path.equals(value);
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
