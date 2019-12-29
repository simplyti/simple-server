@sse @standalone
Feature: SSE server

Scenario: SSE server
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.SSEApi"
	Then I check that "#serviceFuture" is success
	When I get url "http://127.0.0.1:8080/sse" getting stream "#stream"
	Then I check that stream "#stream" contains 2 items
	And I check that item 0 of stream "#stream" is equals to
	"""
	data: Hello!
	
	
	"""
	And I check that item 1 of stream "#stream" is equals to
	"""
	data: Bye!
	
	
	"""
	
