@standalone @clients @httpClient
Feature: Http Client

Scenario: Get request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/hello" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	
Scenario: Delete request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I delete "/delete" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "DELETE!"
	
Scenario: Post request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I post "/echo" with body "Hey!" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hey!"
	
Scenario: Send generic full request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a full post request "/echo" with body "Hey!" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hey!"
	
Scenario: Send request using partial http objects
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a post request "/echo" with content-lenght 12 to stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send content "Hello " to http stream "#stream" getting "#sendResult"
	Then I check that "#sendResult" is success
	When I send last content "Pablo!" to http stream "#stream" getting "#sendResult"
	Then I check that "#sendResult" is success
	And I check that http response "#response" has body "Hello Pablo!"

Scenario: Get request https
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get url "https://localhost:8443/hello" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	
Scenario: Get request with query params
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/queryparams/all" with query params getting response "#response"
		| name	| pablo 	|
		| city	| Madrid	|
	Then I check that "#response" is success
	And I check that http response "#response" has body "{name=[pablo], city=[Madrid]}"
	
Scenario: Http client error
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/responsecode/401" getting response "#response"
	Then I check that "#response" is failure
	And I check that http error of "#response" contains status code 401

Scenario: Ignoring status request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/responsecode/401" ignoring status getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 401
	And I check that http response "#response" has body ""
	
Scenario: Connection error
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/echo" to port 9090 getting response "#response"
	Then I check that "#response" is failure
	And I check that error cause of "#response" contains message "Connection refused: localhost/127.0.0.1:9090"

Scenario: Get http objects
  When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.ChunkedResponseApi"
	Then I check that "#serviceFuture" is success
	When I get url "http://127.0.0.1:8080/stream/5" getting http objects "#objects"
	Then I check that http objects "#objects" contains 7 items
	
Scenario: Get http stream
  When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.ChunkedResponseApi"
	Then I check that "#serviceFuture" is success
	When I get url "http://127.0.0.1:8080/stream/5" getting stream "#stream"
	Then I check that stream "#stream" contains 5 items
	And I check that item 0 of stream "#stream" is equal to "Hello 0"
	And I check that item 1 of stream "#stream" is equal to "Hello 1"
	And I check that item 2 of stream "#stream" is equal to "Hello 2"
	And I check that item 3 of stream "#stream" is equal to "Hello 3"
	And I check that item 4 of stream "#stream" is equal to "Hello 4"

Scenario: Get SSE stream
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.SSEApi"
	Then I check that "#serviceFuture" is success
	When I get url "http://127.0.0.1:8080/sse" getting sse stream "#stream"
	Then I check that stream "#stream" contains 2 items
	
Scenario: Request with response transform
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/hello/json?name=Pablo" getting transformed response to any "#any"
	Then I check that "#any" is success
	And I check that any "#any" has property "message" equals to "Pablo"
	
Scenario: Connection close during request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/close" to port 8080 getting response "#response"
	Then I check that "#response" is failure
	And I check that error cause of "#response" is instancence of "java.nio.channels.ClosedChannelException"