@standalone @clients @httpClient
Feature: Http Client

Background: Start server
  Given I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIExample"
	And I check that "#serviceFuture" is success

Scenario: Get request
	When I get "/get" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello GET!"
	
Scenario: Delete request
	When I delete "/delete" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello DELETE!"
	
Scenario: Post request
	When I post "/post" with body "Hey!" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello POST!"
	
Scenario: Put request
	When I put "/put" with body "Hey!" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello PUT!"
	
Scenario: Patch request
	When I patch "/patch" with body "Hey!" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello PATCH!"
	
Scenario: Options request
	When I options "/options" with body "Hey!" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello OPTIONS!"
	
Scenario: Send generic full request
	When I send a full post request "/echo" with body "Hey!" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hey!"
	
Scenario: Get request processing response to json
	When I get "/get/query-to-json?name=Pablo" getting json response "#any"
	Then I check that "#any" is success
	And I check that json "#any" has property "name" equals to "Pablo"
	
Scenario: Can use an https endpoint
	When I get url "https://localhost:8443/get" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello GET!"
	
Scenario: Can provide an own body content using a body supplier
	When I post "/echo" with body supplier "Hello!" getting json "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	
Scenario: Http client error
	When I get "/status/401" getting response "#response"
	Then I check that "#response" is failure
	And I check that http error of "#response" contains status code 401
	When I get "/status/500" getting response "#response"
	Then I check that "#response" is failure
	And I check that http error of "#response" contains status code 500
	
Scenario: Ignoring status request
	When I get "/status/401" ignoring status getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 401
	And I check that http response "#response" has body ""
	When I post "/echo/status/401" with body "Hey!" ignoring status getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 401
	And I check that http response "#response" has body "Hey!"
	
Scenario: Connection error
	When I get "/get" to port 9090 getting response "#response"
	Then I check that "#response" is failure
	And I check that error cause of "#response" contains message "Connection refused: localhost/127.0.0.1:9090"
	
Scenario: Connection close during request
	When I get "/close" getting response "#response"
	Then I check that "#response" is failure
	And I check that error cause of "#response" is instancence of "java.nio.channels.ClosedChannelException"
	
Scenario: Error during response processing handle
	When I get "/get/query-to-json?name=Pablo" throwing processing error getting response "#response"
	Then I check that "#response" is failure
	And I check that error cause of "#response" is instancence of "java.lang.RuntimeException"

Scenario: Server closing connection after write can cause request errors
	When I execute 1000 serialized get "/get-and-close" getting response error ratio "#errors"
	Then I check that error ratio "#errors" less than 0.4
	When I execute 1000 parallel get "/get-and-close" getting response error ratio "#errors"
	Then I check that error ratio "#errors" less than 0.4
	When I execute 1000 parallel get "/get-and-close?delay=30" getting response error ratio "#errors"
	Then I check that error ratio "#errors" less than 0.4
	
Scenario: Add custom header
	When I get "/get/headers-to-json" with header "x-myheader" "header value" getting json response "#any"
	Then I check that "#any" is success
	And I check that json "#any" has property "x-myheader" equals to "header value"
	
Scenario: Query params request builder
	When I get "/get/query-to-json" with query params getting json response "#any"
		| name	| pablo 	|
		| city	| Madrid	|
	Then I check that "#any" is success
	And I check that json "#any" has property "name" equals to "pablo"
	And I check that json "#any" has property "city" equals to "Madrid"
	When I get "/get/query-to-json" with query param "name" "pablo" getting json response "#any"
	Then I check that "#any" is success
	And I check that json "#any" has property "name" equals to "pablo"
	