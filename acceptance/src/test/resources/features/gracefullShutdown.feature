@shutdown @standalone
Feature: Gracefull shutdown

Scenario: Server stop gracefully awaiting until all requests are precesed
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I asynchronously send a "POST /echo/delay?millis=1000" with body "Hello!" getting "#responseFuture"
	Then I check that "#responseFuture" is not complete
	When I stop server "#serviceFuture" getting "#stopFuture"
	Then I check that "#stopFuture" is success
	And I check that "#responseFuture" is success
	
