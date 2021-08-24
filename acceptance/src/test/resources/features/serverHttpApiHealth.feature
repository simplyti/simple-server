@health @standalone
Feature: Health API

Scenario: Health API
	When I start a service "#serviceFuture"
	Then I check that "#serviceFuture" is success
	When I send a "GET /_health" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "OK"