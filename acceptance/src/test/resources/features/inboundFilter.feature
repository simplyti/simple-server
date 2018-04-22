Feature: Operation Inbound Filter

Scenario: Basic Operation Inbound Filter
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.APITest	|
		| withModule			| com.simplyti.service.OperationInboundFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response"
	And I check that "#response" has status code 401
	
Scenario: Basic Operation Inbound Filter
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.APITest	|
		| withModule			| com.simplyti.service.OperationInboundFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" with authorizarion header "Bearer thetoken" getting "#response"
	And I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello!"