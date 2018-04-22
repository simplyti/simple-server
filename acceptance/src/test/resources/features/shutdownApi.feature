Feature: Shutdown

Scenario: Shutdown API
	When I start a service "#serviceFuture"
	Then I check that "#serviceFuture" is success
	When I send a "GET /_shutdown" getting "#response"
	Then I check that "#response" has status code 204
	And I check that "#serviceFuture" has been shutted down

Scenario: Gracefull shutdown
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I asynchronously send a "POST /echo/delay?millis=1000" with body "Hello!" getting "#responseFuture"
	Then I check that "#responseFuture" is not complete
	When I stop server "#serviceFuture" getting "#stopFuture"
	Then I check that "#stopFuture" is success
	And I check that "#responseFuture" is success
	
