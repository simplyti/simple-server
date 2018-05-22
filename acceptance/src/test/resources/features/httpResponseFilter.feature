Feature: Operation Inbound Filter

Scenario: Basic Http Response filter
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.APITest	|
		| withModule			| com.simplyti.service.HttpResponseFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response"
	And I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello!"