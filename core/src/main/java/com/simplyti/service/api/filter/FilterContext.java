package com.simplyti.service.api.filter;

public interface FilterContext {

	void done();

	String header(CharSequence name);

	void fail(Throwable unauthorizedException);

}
