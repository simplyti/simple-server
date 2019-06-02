Feature: Http Response filter

Scenario: Basic Http Response filter
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.APITest	|
		| withModule		| com.simplyti.service.BasicHttpResponseFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response"
	And I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello!"
	And I check that "#response" contains header "x-filter" equals to "hello"
	
Scenario: Async Http Response filter
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.APITest	|
		| withModule		| com.simplyti.service.AsyncHttpResponseFilterModule |
		| withLog4J2Logger	|		|
		| verbose			|  |
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response"
	And I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello!"
	And I check that "#response" contains header "x-filter" equals to "hello"
	
Scenario: Basic Http Response filter with buffered body
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.APITest	|
		| withModule		| com.simplyti.service.BasicHttpResponseFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "POST /echo/buffered" with 92160 bytes random body getting "#response"
	And I check that "#response" has status code 200
	And I check that "#response" contains header "x-filter" equals to "hello"
	
Scenario: Async Http Response filter with buffered body
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.APITest	|
		| withModule		| com.simplyti.service.AsyncHttpResponseFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "POST /echo/buffered" with 92160 bytes random body getting "#response"
	And I check that "#response" has status code 200
	And I check that "#response" contains header "x-filter" equals to "hello"
	