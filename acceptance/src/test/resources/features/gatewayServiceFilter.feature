@standalone @gateway @filter
Feature: Gateway service filter

Scenario: Gateway service filter can handle request asynchronously
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest	|
		| insecuredPort	| 4444	|
		| securedPort	| -1 |
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayeFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayeFuture" is success
	When I create a service with path "/hello" with filter "com.simplyti.service.examples.filter.AddDelayHttpRequestFilter" and backend "http://127.0.0.1:4444"
	And I create a service with path "/bad" with filter "com.simplyti.service.examples.filter.AsyncFailureHttpRequestFilter" and backend "http://127.0.0.1:4444"
	And I create a service with path "/filter-response" with filter "com.simplyti.service.examples.filter.AsyncReplyHttpRequestFilter" and backend "http://127.0.0.1:4444"
	And I get "/hello" getting response "#response"
	Then I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello!"
	When I get "/bad" getting response "#response"
	Then I check that "#response" is failure
	And I check that http error of "#response" contains status code 400
	When I get "/filter-response" getting response "#response"
	Then I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Filter response!"
	When I get "/hello" getting response "#response"
	Then I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello!"
	
Scenario: Gateway service filter supports chuncked request body
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest	|
		| insecuredPort	| 4444	|
		| securedPort	| -1 |
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayeFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayeFuture" is success
	And I create a service with path "/echo" with filter "com.simplyti.service.examples.filter.AddDelayHttpRequestFilter" and backend "http://127.0.0.1:4444"
	And I create a service with path "/bad" with filter "com.simplyti.service.examples.filter.AsyncFailureHttpRequestFilter" and backend "http://127.0.0.1:4444"
	And I create a service with path "/filter-response" with filter "com.simplyti.service.examples.filter.AsyncReplyHttpRequestFilter" and backend "http://127.0.0.1:4444"
	And I post "/echo" with body stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello stream!"
	When I post "/bad" with body stream "#stream" getting response "#response"
	Then I check that "#response" is failure
	Then I check that http error of "#response" contains status code 400
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream"
	Then I check that "#writeresult" is success
	When I post "/filter-response" with body stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Filter response!"
	
