@sse @standalone
Feature: SSE server

Scenario: SSE server 
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.SSEApi"
	Then I check that "#serviceFuture" is success
	When I get url "http://127.0.0.1:8080/sse" getting stream "#stream"
	Then I check that stream "#stream" contains 2 items
	And I check that item 0 of stream "#stream" is equal to
	"""
	data: Hello!
	
	
	"""
	And I check that item 1 of stream "#stream" is equal to
	"""
	data: Bye!
	
	
	"""
	
Scenario: SSE server using diferent threads
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.SSEApi"
	Then I check that "#serviceFuture" is success
	When I get url "http://127.0.0.1:8080/sse/disrrupt" getting stream "#stream"
	Then I check that stream "#stream" contains 2 items
	And I check that item 0 of stream "#stream" is equal to
	"""
	data: Hello!
	
	
	"""
	And I check that item 1 of stream "#stream" is equal to
	"""
	data: Bye!
	
	
	"""
	
Scenario: SSE server with async response filter
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.SSEApi	|
		| withModule		| com.simplyti.service.examples.filter.AsyncHttpResponseFilterModule |
		| withLog4J2Logger	|		|
		| verbose			|	|
	Then I check that "#serviceFuture" is success
	When I get url "http://127.0.0.1:8080/sse/disrrupt" getting stream "#stream"
	Then I check that stream "#stream" contains 2 items
	And I check that item 0 of stream "#stream" is equal to
	"""
	data: Hello!
	
	
	"""
	And I check that item 1 of stream "#stream" is equal to
	"""
	data: Bye!
	
	
	"""