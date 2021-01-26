@httpHandle @standalone
Feature: Http Handle

Scenario: Basic Http Handle
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response"
	Then I check that "#response" is equals to "Hello!"
	And I check that "#response" has status code 200
	When I send a "GET /hello/" getting "#response"
	Then I check that "#response" is equals to "Hello!"
	And I check that "#response" has status code 200
	And I check that client has 1 active connections
	
Scenario: Basic Http 1.0 Handle
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send an HTTP "1.0" "GET /hello" getting "#response"
	Then I check that "#response" is equals to "Hello!"
	And I check that "#response" has status code 200
	And I check that client has 0 active connections
	
Scenario: Empty response
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /empty" getting "#response"
	Then I check that "#response" is equals to ""
	And I check that "#response" has status code 204
	
Scenario: Echo response
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /echo" with body "Hello super server!" getting "#response"
	Then I check that "#response" is equals to "Hello super server!"
	
Scenario: Echo response with empty body
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /echo" getting "#response"
	Then I check that "#response" is equals to ""
	
Scenario: Not found
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /not/found" getting "#response"
	And I check that "#response" has status code 404
	
Scenario: Not found with body
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /not/found" with body "Hello super server!" getting "#response"
	And I check that "#response" has status code 404
	
Scenario: Failure operation
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /failure?message=Error!" getting "#response"
	And I check that "#response" has status code 500
	
Scenario: Echo response with delay
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /echo/delay?millis=1000" with body "Hello super server!" getting "#response"
	Then I check that "#response" is equals to "Hello super server!"
	
Scenario: Failure operation with delay
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /failure/delay?millis=1000&message=Error!" getting "#response"
	And I check that "#response" has status code 500
	
Scenario: Throwing exception operation
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /throwexception?message=Error!" getting "#response"
	And I check that "#response" has status code 500
	
Scenario: Dynamic path param request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /pathparam/pablo" getting "#response"
	Then I check that "#response" is equals to "Hello pablo"
	When I send a "GET /pathparam/pablo/" getting "#response"
	Then I check that "#response" is equals to "Hello pablo"
	
Scenario: Unexisting path param request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /pathparam/unexisting" getting "#response"
	Then I check that "#response" is equals to "null"
	
Scenario Outline: Query param
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /queryparam?<query>" getting "#response"
	Then I check that "#response" is equals to "<expected>"
	Examples:
	  | query 		| expected		|
	  | name=Pablo	| Hello Pablo	|
	  | other=Pablo	| Hello null		|
	  
Scenario Outline: Query param list
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /queryparams?<query>" getting "#response"
	Then I check that "#response" is equals to "<expected>"
	Examples:
	  | query 				| expected		|
	  | name=Pablo			| Pablo			|
	  | name=Pablo,Arantxa	| Pablo,Arantxa	|
	  
Scenario: Retrieve Uri
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /uri" getting "#response"
	Then I check that "#response" is equals to "Hello /uri"
	
Scenario: Retrieve Headers
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /header/my-header" with header "my-header" with value "service!" getting "#response"
	Then I check that "#response" is equals to "Hello service!"
	
Scenario: Raw response 
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /responsecode/418" getting "#response"
	Then I check that "#response" has status code 418
	
Scenario: Typed request body
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /typed/request" with body '{"message": "Hello Type!"}' getting "#response"
	Then I check that "#response" is equals to "Hello Type!"
	
Scenario: Typed request body null
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /typed/request/tostring" getting "#response"
	Then I check that "#response" is equals to "null"
	
Scenario: Void request body
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /typed/request/void" with body "whatever" getting "#response"
	Then I check that "#response" is equals to "null"
	
Scenario: Typed response body
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /typed/response" getting "#response"
	Then I check that "#response" is equals to '{"message":"Typed response"}'
	
Scenario: Typed request and response body
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /typed/request/response" with body '{"message": "Hello Type!"}' getting "#response"
	Then I check that "#response" is equals to '{"message":"Hello Type!"}'
	
Scenario: Typed request and response body
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /typed/response/request" with body '{"message": "Hello Type!"}' getting "#response"
	Then I check that "#response" is equals to '{"message":"Hello Type!"}'

Scenario: Regex path param
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /wildcard/route/to/resource" getting "#response"
	Then I check that "#response" is equals to "route/to/resource"
	
Scenario: Close connection
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I try to send a "GET /close" getting "#response"
	Then I check that "#response" is failure
	And I check that error failure message of "#response" is "Channel closed"
	
Scenario: Remote connection address
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /remote" getting "#response"
	Then I check that "#response" is equals to "/127.0.0.1"
	
Scenario: Delete operation
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "DELETE /delete" getting "#response"
	Then I check that "#response" is equals to "DELETE!"
	
Scenario: Bad Http request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "THIS IS A BAD METHOD /delete" getting "#response"
	And I check that "#response" has status code 400
	
Scenario: Error after send
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /error/after/send" getting "#response"
	Then I check that "#response" is equals to "I Will send throw an error!"
	And I check that "#response" has status code 200
	And I check that client has 1 active connections
	
Scenario: Conflictive resource path when invoking with trailing slash
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /resources" getting "#response"
	Then I check that "#response" is equals to "This is the resource list"
	When I send a "GET /resources/1" getting "#response"
	Then I check that "#response" is equals to "This is the resource 1"
	When I send a "GET /resources/" getting "#response"
	Then I check that "#response" is equals to "This is the resource list"
	
Scenario: Error encoding json object
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest		|
		| withModule	| com.simplyti.service.JsonWroteErrorModule	|
	Then I check that "#serviceFuture" is success
	When I send a "GET /json/serialize/error" getting "#response"
	And I check that "#response" has status code 500
	Then I check that "#response" is equals to ""
	
	
Scenario: Invocation context can handle futures
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /future" getting "#response"
	Then I check that "#response" is equals to '{"message":"Hello future!"}'
	
Scenario: Invocation context can handle futures with response typed
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /typed/response/future" getting "#response"
	Then I check that "#response" is equals to '{"message":"Hello future!"}'
	
Scenario: Invocation context can handle future with request and response typed
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /typed/request/response/future" with body '{"message": "Hello Type!"}' getting "#response"
	Then I check that "#response" is equals to '{"message":"Hello Type!"}'
	
Scenario: Invocation context can submit sync tasks
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /typed/response/sync" getting "#response"
	Then I check that "#response" match witch '\{"message":"Hello from thread blockingGroup-.*"\}'
	
Scenario: Invocation context can submit void sync tasks
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /void/sync" getting "#response"
	Then I check that "#response" is equals to ''
	And I check that "#response" has status code 204
	
Scenario Outline: Http method <method> handler
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "<method> /<path>" getting "#response"
	Then I check that "#response" is equals to "<method>!"
	When I send a "<method> /<path>?null" getting "#response"
	Then I check that "#response" has status code 204
	When I send a "<method> /<path>/notfoundnull?null" getting "#response"
	Then I check that "#response" has status code 404
	When I send a "<method> /<path>/notfoundnull" getting "#response"
	Then I check that "#response" is equals to "<method>!"
	When I send a "<method> /<path>/dto" getting "#response"
	Then I check that "#response" is equals to '{"message":"<method>!"}'
	When I send a "<method> /<path>/dto?null" getting "#response"
	Then I check that "#response" has status code 204
	When I send a "<method> /<path>/dto/notfoundnull?null" getting "#response"
	Then I check that "#response" has status code 404
	When I send a "<method> /<path>/dto/notfoundnull" getting "#response"
	Then I check that "#response" is equals to '{"message":"<method>!"}'
	When I send a "<method> /<path>/notfoundnull/dto?null" getting "#response"
	Then I check that "#response" has status code 404
	When I send a "<method> /<path>/notfoundnull/dto" getting "#response"
	Then I check that "#response" is equals to '{"message":"<method>!"}'
	Examples:
	| method 	| path 		|
	| GET 		| get    	|
	| DELETE	| delete 	|
	| POST		| post 		|
	| PUT		| put 		|
	| PATCH		| patch		|
	
Scenario Outline: Http body method <method> handler
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "<method> /<path>/dto/echo" with body '{"message":"<method>!"}' getting "#response"
	Then I check that "#response" is equals to '{"message":"<method>!"}'
	Examples:
	| method 	| path 		|
	| POST		| post 		|
	| PUT		| put 		|
	| PATCH		| patch		|
	

	
	
	
	
