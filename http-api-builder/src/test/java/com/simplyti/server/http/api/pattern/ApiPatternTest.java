package com.simplyti.server.http.api.pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;


public class ApiPatternTest {
	
	@Test
	public void buildEmpty() {
		ApiPattern pattern = ApiPattern.build("/");
		
		assertThat(pattern.variableCount(),equalTo(0));
		assertTrue(pattern.matcher("/").matches());
	}
	
	@Test
	public void buildSimple() {
		ApiPattern pattern = ApiPattern.build("/users");
		
		assertThat(pattern.variableCount(),equalTo(0));
		assertThat(pattern.literalCharsCount(),equalTo(6));
		assertTrue(pattern.matcher("/users").matches());
		assertTrue(pattern.matcher("/users/").matches());
		assertFalse(pattern.matcher("/users/1").matches());
	}
	
	@Test
	public void buildWithVariables() {
		ApiPattern pattern = ApiPattern.build("/users/{id}");
		
		assertThat(pattern.variableCount(),equalTo(1));
		assertThat(pattern.literalCharsCount(),equalTo(7));
		assertTrue(pattern.matcher("/users/4").matches());
		assertTrue(pattern.matcher("/users/4/").matches());
		assertFalse(pattern.matcher("/users/4/info").matches());
	}

}
