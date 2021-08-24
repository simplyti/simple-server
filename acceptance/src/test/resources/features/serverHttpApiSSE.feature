@sse @standalone
Feature: SSE server

Scenario: SSE server 
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.SSEApi"
	Then I check that "#serviceFuture" is success
	When I get "/sse" handling objects "#objects" and getting result "#result"
	Then I check that "#result" is success
	And I check that object list "#objects" has size 2
	And I check that item 0 of stream "#objects" is equal to
	"""
	data: Hello
	
	
	"""
	And I check that item 1 of stream "#objects" is equal to
	"""
	data: Bye
	
	
	"""
	
Scenario: SSE server using diferent threads
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.SSEApi"
	Then I check that "#serviceFuture" is success
	When I get "/sse/disrrupt" handling objects "#objects" and getting result "#result"
	Then I check that "#result" is success
	And I check that object list "#objects" has size 2
	And I check that item 0 of stream "#objects" is equal to
	"""
	data: Hello!
	
	
	"""
	And I check that item 1 of stream "#objects" is equal to
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
	When I get "/sse/disrrupt" handling objects "#objects" and getting result "#result"
	Then I check that "#result" is success
	And I check that object list "#objects" has size 2
	And I check that item 0 of stream "#objects" is equal to
	"""
	data: Hello!
	
	
	"""
	And I check that item 1 of stream "#objects" is equal to
	"""
	data: Bye!
	
	
	"""