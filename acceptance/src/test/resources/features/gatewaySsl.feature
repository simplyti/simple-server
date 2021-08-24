@gateway @standalone
Feature: Gateway Ssl

Scenario: Getting response redirect to https
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest	|
		| listener		| 4444	|
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayeFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
		| verbose | |
	Then I check that "#gatewayeFuture" is success
	When I create an ssl service with path "/hello" and backend "http://127.0.0.1:4444"
	And I get "/hello" getting response "#response"
	Then I check that http response "#response" has status code 301
	And I check that http response "#response" has body ""
	And I check that http response "#response" contains header "location" equals to "https://localhost/hello"
	When I get url "https://localhost:8443/hello" getting response "#response"
	Then I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello!"
