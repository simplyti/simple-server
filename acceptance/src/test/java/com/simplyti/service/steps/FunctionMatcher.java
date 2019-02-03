package com.simplyti.service.steps;

import java.util.function.Function;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class FunctionMatcher<T,O> extends TypeSafeDiagnosingMatcher<T> {
	
	private final Matcher<O> matcher;
	private final Function<T, O> function;

	public FunctionMatcher(Function<T,O> function,Matcher<O> matcher) {
		this.matcher=matcher;
		this.function=function;
	}

	@Override
	public void describeTo(Description description) {}

	@Override
	protected boolean matchesSafely(T item, Description mismatchDescription) {
		return matcher.matches(this.function.apply(item));
	}

}
