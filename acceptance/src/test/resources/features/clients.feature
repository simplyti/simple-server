Feature: Clients

Scenario: Connection is reused
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
	
Scenario: Connection error
	When I get "/hello" getting response "#response"
	And I check that "#response" is failure
	
Scenario: Single thread client
	Given a single thread event loop group "#eventLoopGroup"
	When I create an http client "#client" with event loop group "#eventLoopGroup"
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/hello" using client "#client" in event loop "#eventLoopGroup" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	When I get "/hello" using client "#client" in event loop "#eventLoopGroup" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	And I check that http client "#client" has 1 iddle connection
	
Scenario: Connection write stream with http objecs
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I post "/echo" with body stream "#stream", content part "Hello ", length of 20 getting response objects "#responseStream"
	And I check that stream "#stream" is not complete
	When I send "stream." to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that stream "#stream" is not complete
	When I send last "The end" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that stream "#stream" is success
	And I check that response stream "#responseStream" contains body "Hello stream.The end"
	And I check that http client has 1 iddle connection
	When I post "/echo" with body stream "#stream", content part "Bye", length of 4 getting response objects "#responseStream"
	And I check that stream "#stream" is not complete
	When I send last "!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that stream "#stream" is success
	And I check that response stream "#responseStream" contains body "Bye!"
	And I check that http client has 1 iddle connection
	
Scenario: Single thread stream with http objects
	Given a single thread event loop group "#eventLoopGroup"
	When I create an http client "#client" with event loop group "#eventLoopGroup"
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I post "/echo" using client "#client" with body stream "#stream", content part "Hello ", length of 20 getting response objects "#responseStream"
	And I check that stream "#stream" is not complete
	When I send "stream." to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that stream "#stream" is not complete
	When I send last "The end" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that stream "#stream" is success
	And I check that response stream "#responseStream" contains body "Hello stream.The end"
	And I check that http client "#client" has 1 iddle connection
	When I post "/echo" using client "#client" with body stream "#stream", content part "Bye", length of 4 getting response objects "#responseStream"
	And I check that stream "#stream" is not complete
	When I send last "!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that stream "#stream" is success
	And I check that response stream "#responseStream" contains body "Bye!"
	And I check that http client "#client" has 1 iddle connection
	
Scenario: Connection error when write stream
	When I post "/echo" with body stream "#stream", content part "Hello", length of 6 getting response objects "#responseStream"
	And I check that stream "#stream" is failure
	When I send "!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is failure
	
Scenario: Connection closed
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/close" getting response "#response"
	And I check that "#response" is failure
	And I check that "#response" has conention closed failure
	
Scenario: Client custom tracer
	Given a simple client request tracer "#tracer"
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/hello" getting response "#response" with request tracer "#tracer"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	And I check that tracer "#tracer" contains 1 request
