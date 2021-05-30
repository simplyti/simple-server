@apiFilter @standalone
Feature: Operation Inbound Filter

Scenario: Basic Operation Inbound Filter
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest	|
		| withModule		| com.simplyti.service.examples.filter.OperationInboundFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 401
	And I check that http response "#response" has body ""
	
Scenario: Basic Operation Inbound Filter
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest	|
		| withModule		| com.simplyti.service.examples.filter.OperationInboundFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" with authorizarion header "Bearer thetoken" getting "#response"
	And I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello!"