@serverBuilder @standalone
Feature: Builder

Scenario: I cannot start servers on same port
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| listener			| 8080	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| listener			| 8080	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is failure
	And I check that error failure message of "#serviceFuture" contains "Address already in use"

Scenario: I can specify multiple apis
	When I start a service "#serviceFuture" with options:
		| option	| value |
		| withApi	| com.simplyti.service.examples.api.APIExample		|
		| withApi	| com.simplyti.service.examples.api.OtherAPITest	|
	Then I check that "#serviceFuture" is success
	When I send a "GET /get" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello GET!"
	When I send a "GET /other/hello" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"

Scenario: I can use custom json module
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APIExample		|
		| withApi		| com.simplyti.service.examples.api.OtherAPITest	|
	Then I check that "#serviceFuture" is success
	When I send a "GET /get/query-to-json?name=Pablo" getting json response "#any"
	Then I check that "#any" is success
	And I check that json "#any" has property "name" equals to "Pablo"
	
Scenario: I can use custom eventloop group
	When I start a service "#serviceFuture" with options:
		| option	 						| value |
		| withApi							| com.simplyti.service.examples.api.APIExample		|
		| withEventLoopGroup	| #managed	|
	Then I check that "#serviceFuture" is success
	When I send a "GET /get/query-to-json?name=Pablo" getting json response "#any"
	Then I check that "#any" is success
	And I check that json "#any" has property "name" equals to "Pablo"
	
