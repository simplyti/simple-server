package com.simplyti.service.matcher;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;

public class RegexApiMatcher implements ApiMatcher {

	private final Matcher matcher;
	private final boolean matches;

	public RegexApiMatcher(Pattern pattern, String value) {
		this.matcher = pattern.matcher(value);
		this.matches = this.matcher.matches();
	}

	@Override
	public boolean matches() {
		return matches;
	}

	@Override
	public String group(int group) {
		return matcher.group(group);
	}

}
