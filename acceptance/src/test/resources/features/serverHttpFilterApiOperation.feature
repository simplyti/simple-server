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
	When I send a "GET /hello" with authorizarion header "Bearer thetoken" getting "#response"
	And I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello!"

Scenario: Operation Inbound Filter handle response
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest	|
		| withModule		| com.simplyti.service.examples.filter.OperationInboundFilterModule |
		| withLog4J2Logger	|		|
		| verbose | |
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello?handle" with authorizarion header "Bearer thetoken" getting "#response"
	And I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello filter handle!"
	When I send a "GET /hello" with authorizarion header "Bearer thetoken" getting "#response"
	And I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello!"
	
Scenario: Operation Inbound Filter in stream API
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest	|
		| withApi			| com.simplyti.service.examples.api.StreamAPITest	|
		| withModule		| com.simplyti.service.examples.filter.OperationInboundFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I post "/echo/chunked" with chunked body stream "#stream" getting response "#response"
	Then I check that "#response" is failure
	And I check that http error of "#response" contains status code 401
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream" getting result "#result"
	Then I check that "#result" is success
	When I get "/hello" with bearer auth "thetoken" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	When I post "/echo/chunked" with chunked body stream "#stream" getting response "#response"
	Then I check that "#response" is failure
	And I check that http error of "#response" contains status code 401
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream" getting result "#result"
	Then I check that "#result" is success
	When I post "/echo/chunked" with bearer auth "thetoken" and chunked body stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#response" is not complete
	When I close request stream "#stream"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello stream!"
	
Scenario: Operation Inbound Filter delayed in stream API
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest	|
		| withApi			| com.simplyti.service.examples.api.StreamAPITest	|
		| withModule		| com.simplyti.service.examples.filter.OperationInboundFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I post "/echo/chunked?delay=500" with chunked body stream "#stream" getting response "#response"
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream" getting result "#result"
	Then I check that "#result" is success
	Then I check that "#response" is failure
	And I check that http error of "#response" contains status code 401
	When I get "/hello" with bearer auth "thetoken" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	When I post "/echo/chunked?delay=500" with bearer auth "thetoken" and chunked body stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#response" is not complete
	When I close request stream "#stream"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello stream!"
		
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