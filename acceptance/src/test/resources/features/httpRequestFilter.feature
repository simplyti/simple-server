Feature: Http Request filter

Scenario: Basic Http Request filter
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.APITest	|
		| withModule		| com.simplyti.service.BasicHttpRequestFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello/1" getting "#response"
	And I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello 1!"
	When I send a "GET /hello/bad" getting "#response"
	And I check that "#response" has status code 400

Scenario: Async Http Request filter
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.APITest	|
		| withModule		| com.simplyti.service.AsyncHttpRequestFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello/1" getting "#response"
	And I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello 1!"
	When I send a "GET /hello/bad" getting "#response"
	And I check that "#response" has status code 400