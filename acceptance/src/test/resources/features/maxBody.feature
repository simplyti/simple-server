@maxBody @standalone
Feature: Maximun body

Scenario: Maximun body
	When I start a service "#serviceFuture" with max body size 10 with API "com.simplyti.service.examples.api.MaximunBodyTestApi"
	Then I check that "#serviceFuture" is success
	When I send a "POST /maximun10" with body "Hello!" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello!"
	When I send a "POST /maximun10" with body "This is a too long message!" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 413
	And I check that http response "#response" has body ""
