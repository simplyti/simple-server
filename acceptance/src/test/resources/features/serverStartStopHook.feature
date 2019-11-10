@standalone
Feature: Server start/stop hook

Scenario: Server start hook
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule			| com.simplyti.service.examples.hook.TestServerStartHookModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hook" getting "#response"
	And I check that "#response" is equals to "HOOK!"
	
Scenario: Server start hook failure
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.examples.hook.TestServerStartHookFailModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is failure
	And I check that error failure message of "#serviceFuture" is "Hook Error"
	
Scenario: Server stop hook
	Given a server stop hook module "#stopHookModule"
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest	|
		| withModule		| #stopHookModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I stop server "#serviceFuture" getting "#stopFuture"
	Then I check that "#stopFuture" is success
	And I check that stop hook in "#stopHookModule" was invoked
