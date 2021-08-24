@httpHandle @standalone
Feature: Http Handle 

Scenario Outline: Handle basic HTTP <method> <path> request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIExample"
	Then I check that "#serviceFuture" is success
	When I send a "<method> <path>" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code <expectedStatus>
	And I check that http response "#response" has body "<expectedBody>"
	Examples:
	  | method	| path 					| expectedStatus 	| expectedBody		| 
	  | GET			| /get					| 200							| Hello GET!			|
	  | GET			| /get/					| 200							| Hello GET!			|
	  | GET			| /notfound			| 404							| 								|
	  | POST		| /post					| 200							| Hello POST!			|
	  | POST		| /post/				| 200							| Hello POST!			|
	  | POST		| /notfound			| 404							| 								|
	  | DELETE	| /delete				| 200							| Hello DELETE!		|
	  | DELETE	| /delete/			| 200							| Hello DELETE!		|
	  | DELETE	| /notfound			| 404							| 								|
	  | PUT			| /put					| 200							| Hello PUT!			|
	  | PUT			| /put/					| 200							| Hello PUT!			|
	  | PUT			| /notfound			| 404							| 								|
	  | PATCH		| /patch				| 200							| Hello PATCH!		|
	  | PATCH		| /patch/				| 200							| Hello PATCH!		|
	  | PATCH		| /notfound			| 404							| 								|
	  | OPTIONS	| /options			| 200							| Hello OPTIONS!	|
	  | OPTIONS	| /options/			| 200							| Hello OPTIONS!	|
	  | OPTIONS	| /notfound			| 404							| 								|
	  
Scenario Outline: Handle payloadable HTTP <method> <path> request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIExample"
	Then I check that "#serviceFuture" is success
	When I send a "<method> <path>" with body "<body>" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code <expectedStatus>
	And I check that http response "#response" has body "<expectedBody>"
	Examples:
	  | method	| path 									| body		| expectedStatus 	| expectedBody	|
	  | POST		| /post/echo						| Hello!	| 200							| Hello!				|
	  | POST		| /post/echo?delay=200	| Hello!	| 200							| Hello!				|
	  | POST		| /post/echo						| 				| 200							| 							|
	  | POST		| /notfound							| 				| 404							| 							|
	  | PUT			| /put/echo							| Hello!	| 200							| Hello!				|
	  | PUT			| /put/echo?delay=200		| Hello!	| 200							| Hello!				|
	  | PUT			| /put/echo							| 				| 200							| 							|
	  | PUT			| /notfound							| 				| 404							| 							|
	  | PATCH		| /patch/echo						| Hello!	| 200							| Hello!				|
	  | PATCH		| /patch/echo?delay=200	| Hello!	| 200							| Hello!				|
	  | PATCH		| /patch/echo						|					| 200							| 							|
	  | PATCH		| /notfound							| 				| 404							| 							|
	  
Scenario Outline: Handle future result HTTP <method> <path> request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIExample"
	Then I check that "#serviceFuture" is success
	When I send a "<method> <path>" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code <expectedStatus>
	And I check that http response "#response" has body "<expectedBody>"
	Examples:
	  | method	| path 						| expectedStatus 	| expectedBody		| 
	  | GET			| /get/future			| 200							| Hello GET!			|
	  | POST		| /post/future		| 200							| Hello POST!			|
	  | DELETE	| /delete/future	| 200							| Hello DELETE!		|
	  | PUT			| /put/future			| 200							| Hello PUT!			|
	  | PATCH		| /patch/future		| 200							| Hello PATCH!		|
	  | OPTIONS	| /options/future	| 200							| Hello OPTIONS!	|
	  
Scenario Outline: Handle future typed result HTTP <method> <path> request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIExample"
	Then I check that "#serviceFuture" is success
	When I send a "<method> <path>" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code <expectedStatus>
	And I check that http response "#response" has body '<expectedBody>'
	Examples:
	  | method	| path 													| expectedStatus 	| expectedBody									| 
	  | GET			| /get/response-type						| 200							| {"message":"Hello GET!"}			|
	  | GET			| /get/response-type/future			| 200							| {"message":"Hello GET!"}			|
	  | POST		| /post/response-type						| 200							| {"message":"Hello POST!"}			|
	  | POST		| /post/response-type/future		| 200							| {"message":"Hello POST!"}			|
	  | DELETE	| /delete/response-type					| 200							| {"message":"Hello DELETE!"}		|
	  | DELETE	| /delete/response-type/future	| 200							| {"message":"Hello DELETE!"}		|
	  | PUT			| /put/response-type						| 200							| {"message":"Hello PUT!"}			|
	  | PUT			| /put/response-type/future			| 200							| {"message":"Hello PUT!"}			|
	  | PATCH		| /patch/response-type					| 200							| {"message":"Hello PATCH!"}		|
	  | PATCH		| /patch/response-type/future		| 200							| {"message":"Hello PATCH!"}		|
	  | OPTIONS	| /options/response-type				| 200							| {"message":"Hello OPTIONS!"}	|
	  | OPTIONS	| /options/response-type/future	| 200							| {"message":"Hello OPTIONS!"}	|
	  
Scenario Outline: Handle templated path HTTP <method> <path> request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIExample"
	Then I check that "#serviceFuture" is success
	When I send a "<method> <path>" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code <expectedStatus>
	And I check that http response "#response" has body "<expectedBody>"
	Examples:
	  | method	| path 												| expectedStatus 	| expectedBody						| 
	  | GET			| /get-numeric-id/12345				| 200							| Hello GET 12345!				|
	  | GET			| /get-numeric-id/12345/			| 200							| Hello GET 12345!				|
	  | GET			| /get-numeric-id/aaa					| 404							| 												|
	  | GET			| /get-id/aaa									| 200							| Hello GET aaa!					|
	  | GET			| /get-any/aaa/bbb						| 200							| Hello GET aaa/bbb!			|
	  | POST		| /post-numeric-id/12345			| 200							| Hello POST 12345!				|
	  | POST		| /post-numeric-id/12345/			| 200							| Hello POST 12345!				|
	  | POST		| /post-numeric-id/aaa				| 404							| 												|
	  | POST		| /post-id/aaa								| 200							| Hello POST aaa!					|
	  | POST		| /post-any/aaa/bbb						| 200							| Hello POST aaa/bbb!			|
	  | DELETE	| /delete-numeric-id/12345		| 200							| Hello DELETE 12345!			|
	  | DELETE	| /delete-numeric-id/12345/		| 200							| Hello DELETE 12345!			|
	  | DELETE	| /delete-numeric-id/aaa			| 404							| 												|
	  | DELETE	| /delete-id/aaa							| 200							| Hello DELETE aaa!				|
	  | DELETE	| /delete-any/aaa/bbb					| 200							| Hello DELETE aaa/bbb!		|
	  | PUT			| /put-numeric-id/12345				| 200							| Hello PUT 12345!				|
	  | PUT			| /put-numeric-id/12345/			| 200							| Hello PUT 12345!				|
	  | PUT			| /put-numeric-id/aaa					| 404							| 												|
	  | PUT			| /put-id/aaa									| 200							| Hello PUT aaa!					|
	  | PUT			| /put-any/aaa/bbb						| 200							| Hello PUT aaa/bbb!			|
	  | PATCH		| /patch-numeric-id/12345			| 200							| Hello PATCH 12345!			|
	  | PATCH		| /patch-numeric-id/12345/		| 200							| Hello PATCH 12345!			|
	  | PATCH		| /patch-numeric-id/aaa				| 404							| 												|
	  | PATCH		| /patch-id/aaa								| 200							| Hello PATCH aaa!				|
	  | PATCH		| /patch-any/aaa/bbb					| 200							| Hello PATCH aaa/bbb!		|
	  | OPTIONS	| /options-numeric-id/12345		| 200							| Hello OPTIONS 12345!		|
	  | OPTIONS	| /options-numeric-id/12345/	| 200							| Hello OPTIONS 12345!		|
	  | OPTIONS	| /options-numeric-id/aaa			| 404							| 												|
	  | OPTIONS	| /options-id/aaa							| 200							| Hello OPTIONS aaa!			|
	  | OPTIONS	| /options-any/aaa/bbb				| 200							| Hello OPTIONS aaa/bbb!	|
	  
Scenario Outline: Empty response <method> <path>
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIExample"
	Then I check that "#serviceFuture" is success
	When I send a "<method> <path>" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 204
	And I check that http response "#response" has body ""
	Examples:
	  | method	| path 						| 
	  | GET			| /get-empty			|
	  | POST		| /post-empty			|
	  | DELETE	| /delete-empty		|
	  | PUT			| /put-empty			|
	  | PATCH		| /patch-empty		|
	  | OPTIONS	| /options-empty	|
	  
Scenario Outline: Empty response on null <method> <path>
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIExample"
	Then I check that "#serviceFuture" is success
	When I send a "<method> <path>" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 204
	And I check that http response "#response" has body ""
	Examples:
	  | method	| path 						| 
	  | GET			| /get-null				|
	  | POST		| /post-null			|
	  | DELETE	| /delete-null		|
	  | PUT			| /put-null				|
	  | PATCH		| /patch-null			|
	  | OPTIONS	| /options-null		|
	  
Scenario Outline: Not found response on null <method> <path>
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIExample"
	Then I check that "#serviceFuture" is success
	When I send a "<method> <path>" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 404
	And I check that http response "#response" has body ""
	Examples:
	  | method	| path 										| 
	  | GET			| /get-null-notfound			|
	  | POST		| /post-null-notfound			|
	  | DELETE	| /delete-null-notfound		|
	  | PUT			| /put-null-notfound			|
	  | PATCH		| /patch-null-notfound		|
	  | OPTIONS	| /options-null-notfound	|
	  
	
Scenario: Basic Http 1.0 Handle
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send an HTTP "1.0" "GET /hello" getting "#response"
	Then I check that "#response" is equals to "Hello!"
	And I check that "#response" has status code 200
	And I check that client has 0 active connections
	
Scenario: Failure operation
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /failure?message=Error!" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 500
	And I check that http response "#response" has body ""
	
Scenario: Failure operation with delay
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /failure/delay?millis=1000&message=Error!" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 500
	And I check that http response "#response" has body ""
	
Scenario: Throwing exception operation
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /throwexception?message=Error!" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 500
	And I check that http response "#response" has body ""
	
Scenario: Dynamic path param request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /pathparam/pablo" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello pablo"
	When I send a "GET /pathparam/pablo/" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello pablo"
	
Scenario: Unexisting path param request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /pathparam/unexisting" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "null"
	
Scenario Outline: Query param
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /queryparam?<query>" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "<expected>"
	Examples:
	  | query 			| expected			|
	  | name=Pablo	| Hello Pablo		|
	  | other=Pablo	| Hello null		|
	  
Scenario Outline: Query param list
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /queryparams?<query>" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "<expected>"
	Examples:
	  | query 							| expected			|
	  | name=Pablo					| Pablo					|
	  | name=Pablo,Arantxa	| Pablo,Arantxa	|
	  
Scenario: Retrieve Uri
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /uri" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello /uri"
	
Scenario: Retrieve Headers
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /header/my-header" with header "my-header" with value "service!" getting "#response"
	Then I check that "#response" is equals to "Hello service!"
	
Scenario: Raw response 
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /responsecode/418" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 418
	And I check that http response "#response" has body ""
	
Scenario: Typed request body
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /typed/request" with body '{"message": "Hello Type!"}' getting "#response"
	Then I check that "#response" is equals to "Hello Type!"
	
Scenario: Typed request body null
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /typed/request/tostring" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "null"
	
Scenario: Void request body
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /typed/request/void" with body "whatever" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "null"
	
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
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "/127.0.0.1"
	
Scenario: Bad Http request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "THIS IS A BAD METHOD /delete" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 400
	And I check that http response "#response" has body ""
	
Scenario: Error after send
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /error/after/send" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "I Will send throw an error!"
	
Scenario: Conflictive resource path when invoking with trailing slash
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /resources" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "This is the resource list"
	When I send a "GET /resources/1" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "This is the resource 1"
	When I send a "GET /resources/" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "This is the resource list"
	
Scenario: Error encoding json object
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest		|
		| withModule	| com.simplyti.service.JsonWroteErrorModule	|
	Then I check that "#serviceFuture" is success
	When I send a "GET /json/serialize/error" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 500
	And I check that http response "#response" has body ""
	
Scenario: Invocation context can handle futures
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /future" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body '{"message":"Hello future!"}'
	
Scenario: Invocation context can handle futures with response typed
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /typed/response/future" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body '{"message":"Hello future!"}'
	
Scenario: Invocation context can handle future with request and response typed
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /typed/request/response/future" with body '{"message": "Hello Type!"}' getting "#response"
	Then I check that "#response" is equals to '{"message":"Hello Type!"}'
	
Scenario: Invocation context can submit sync tasks
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /typed/response/sync" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" match witch '\{"message":"Hello from thread blockingGroup-.*"\}'
	
Scenario: Invocation context can submit void sync tasks
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /void/sync" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 204
	And I check that http response "#response" has body ""
	
Scenario Outline: Http method <method> handler
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "<method> /<path>" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "<method>!"
	When I send a "<method> /<path>?null" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 204
	And I check that http response "#response" has body ""
	When I send a "<method> /<path>/notfoundnull?null" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 404
	And I check that http response "#response" has body ""
	When I send a "<method> /<path>/notfoundnull" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "<method>!"
	When I send a "<method> /<path>/dto" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body '{"message":"<method>!"}'
	When I send a "<method> /<path>/dto?null" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 204
	And I check that http response "#response" has body ""
	When I send a "<method> /<path>/dto/notfoundnull?null" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 404
	And I check that http response "#response" has body ""
	When I send a "<method> /<path>/dto/notfoundnull" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body '{"message":"<method>!"}'
	When I send a "<method> /<path>/notfoundnull/dto?null" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 404
	And I check that http response "#response" has body ""
	When I send a "<method> /<path>/notfoundnull/dto" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body '{"message":"<method>!"}'
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
	

	
	
	
	
