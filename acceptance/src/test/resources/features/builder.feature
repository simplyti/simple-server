Feature: Builder

Scenario: I cannot specify logger factory twice
	When I try to start a service "#serviceFuture" with options getting error "#error":
		| option	 			| value |
		| withLog4J2Logger	|		|
		| withLog4J2Logger	|		|
	Then I check that error "#error" contains message "Logger already stablished to log4j2"
	
Scenario: I cannot start servers on same port
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| insecuredPort		| 8080	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| insecuredPort		| 8080	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is failure
	And I check that error failure message of "#serviceFuture" contains "Address already in use"

Scenario: I specify multiple apis
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi	| com.simplyti.service.APITest		|
		| withApi	| com.simplyti.service.OtherAPITest	|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response"
	Then I check that "#response" is equals to "Hello!"
	When I send a "GET /other/hello" getting "#response"
	Then I check that "#response" is equals to "Hello!"
	
Scenario: I can use differend json module
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.APITest		|
		| withApi		| com.simplyti.service.OtherAPITest	|
		| withModule	| com.simplyti.service.steps.JsoniterModule |
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response"
	Then I check that "#response" is equals to "Hello!"
	When I send a "GET /other/hello" getting "#response"
	Then I check that "#response" is equals to "Hello!"