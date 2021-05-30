@requestFilter @standalone
Feature: Http Request filter

Scenario: Basic Http Request filter
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest	|
		| withModule		| com.simplyti.service.examples.filter.BasicHttpRequestFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello/1" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello 1!"
	When I send a "GET /hello/bad" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 400
	And I check that http response "#response" has body ""

Scenario: Async Http Request filter
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest	|
		| withModule		| com.simplyti.service.examples.filter.AsyncHttpRequestFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello/1" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello 1!"
	When I send a "GET /hello/bad" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 400
	And I check that http response "#response" has body ""
	
Scenario: Basic Http Request filter with buffered body
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest	|
		| withModule		| com.simplyti.service.examples.filter.BasicHttpRequestFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "POST /echo/1" with 9216 bytes random body getting "#response"
	And I check that "#response" has status code 200
	When I send a "POST /echo/bad" with 9216 bytes random body getting "#response"
	And I check that "#response" has status code 400

Scenario: Async Http Request filter with buffered body
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest	|
		| withModule		| com.simplyti.service.examples.filter.AsyncHttpRequestFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "POST /echo/1" with 92160 bytes random body getting "#response"
	And I check that "#response" has status code 200
	When I send a "POST /echo/bad" with 92160 bytes random body getting "#response"
	And I check that "#response" has status code 400
	
Scenario: Full Http Request filter
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest	|
		| withModule		| com.simplyti.service.examples.filter.FullHttpRequestFilterModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello/1" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello 1!"
	When I send a "GET /hello/bad" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 400
	And I check that http response "#response" has body ""