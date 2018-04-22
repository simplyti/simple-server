Feature: Clients

Scenario: Connection are reused
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/hello" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	And I check that http client has 1 iddle connection
	When I get "/hello" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	And I check that http client has 1 iddle connection
	
Scenario: New connection are created if needed
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I post "/echo/delay?millis=500" with body "Hey 1!" getting response "#response1"
	And I check that http client has 1 active connection
	And I post "/echo/delay?millis=500" with body "Hey 2!" getting response "#response2"
	And I check that http client has 2 active connection
	Then I check that "#response1" is success
	And I check that "#response2" is success
	And I check that http client has 2 iddle connection
	And I check that http response "#response1" has body "Hey 1!"
	And I check that http response "#response2" has body "Hey 2!"
	When I get "/hello" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	And I check that http client has 2 iddle connection
	And I check that http client has 2 total connection
	
Scenario: Request timeout
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I post "/echo/delay?millis=500" with body "Hey!" and response time 700 getting response "#response"
	And I check that "#response" is success
	And I check that http response "#response" has body "Hey!"
	When I post "/echo/delay?millis=500" with body "Hey!" and response time 200 getting response "#response"
	Then I check that "#response" is failure
