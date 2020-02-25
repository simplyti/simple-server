package com.simplyti.server.http.api.pattern;

public interface ApiMatcher {

	boolean matches();
	String group(int group);

}
