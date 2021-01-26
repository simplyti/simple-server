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
	When I post "/post" with body "Hey!" getting response "#response"
	Then I check that "#response" is success
		And I check that http response "#response" has body "POST!"
	
Scenario: Put request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I put "/put" with body "Hey!" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "PUT!"
	
Scenario: Patch request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I patch "/patch" with body "Hey!" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "PATCH!"
	
Scenario: Options request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I options "/options" with body "Hey!" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "OPTIONS!"
	
Scenario: Get request processing response to json
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/hello/json?name=Pablo" getting json response "#any"
	Then I check that "#any" is success
	And I check that json "#any" has property "message" equals to "Pablo"
	
Scenario: Send generic full request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a full post request "/echo" with body "Hey!" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hey!"
	
Scenario: Use an https endpoint
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get url "https://localhost:8443/hello" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	
Scenario: Request builder body supplier
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I post "/echo" with body supplier "Hello!" getting json "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	
Scenario: Http client error
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/responsecode/401" getting response "#response"
	Then I check that "#response" is failure
	And I check that http error of "#response" contains status code 401
	When I get "/responsecode/500" getting response "#response"
	Then I check that "#response" is failure
	And I check that http error of "#response" contains status code 500
	
Scenario: Ignoring status request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/responsecode/401" ignoring status getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 401
	And I check that http response "#response" has body ""
	When I post "/responsecode/401" with body "Hey!" ignoring status getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 401
	And I check that http response "#response" has body "Hey!"
	
Scenario: Connection error
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/echo" to port 9090 getting response "#response"
	Then I check that "#response" is failure
	And I check that error cause of "#response" contains message "Connection refused: localhost/127.0.0.1:9090"
	
Scenario: Connection close during request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/close" to port 8080 getting response "#response"
	Then I check that "#response" is failure
	And I check that error cause of "#response" is instancence of "java.nio.channels.ClosedChannelException"
	
Scenario: Error during response processing handle
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/hello/json?name=Pablo" throwing processing error getting response "#response"
	Then I check that "#response" is failure
	And I check that error cause of "#response" is instancence of "java.lang.RuntimeException"

Scenario: Server closing connection can cause request errors
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I execute 1000 serialized get "/hello/close" getting response error ratio "#errors"
	Then I check that error ratio "#errors" less than 0.1
	When I execute 1000 parallel get "/hello/close" getting response error ratio "#errors"
	Then I check that error ratio "#errors" less than 0.3
	When I execute 1000 parallel get "/hello/close?delay=30" getting response error ratio "#errors"
	Then I check that error ratio "#errors" less than 0.3
	
Scenario: Add custom header
  When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I post "/echo/request" with body "Hey!" and header "x-myheader" "header value" getting json response "#response"
	Then I check that "#response" is success
	And I check that json "#response" has property "body" equals to "Hey!"
	And I check that json "#response" has property "headers.x-myheader" equals to "header value"
	
Scenario: Query params request builder
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/queryparams/all" with query params getting response "#response"
		| name	| pablo 	|
		| city	| Madrid	|
	Then I check that "#response" is success
	And I check that http response "#response" has body "{name=[pablo], city=[Madrid]}"
	When I post "/echo/request" with body "Hey!" and query param "name" "pablo" getting json response "#response"
	Then I check that "#response" is success
	And I check that json "#response" has property "params.name" equals to "pablo"