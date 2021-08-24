@standalone @clients
Feature: Clients

Background: Start server
  Given I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIExample"
	And I check that "#serviceFuture" is success

Scenario: Connection is reused when using pooled client
	When I get "/get" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello GET!"
	And I check that http client has 1 iddle connection
	When I get "/get" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello GET!"
	And I check that http client has 1 iddle connection

Scenario: New connection is created if needed when use unlimited pooled client
	When I post "/echo?delay=500" with body "Hey 1!" getting response "#response1"
	Then I check that http client has 1 active connection
	When I post "/echo?delay=500" with body "Hey 2!" getting response "#response2"
	And I check that http client has 2 active connection
	And I check that "#response1" is success
	And I check that "#response2" is success
	And I check that http client has 2 iddle connection
	And I check that http response "#response1" has body "Hey 1!"
	And I check that http response "#response2" has body "Hey 2!"
	When I get "/get" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello GET!"
	And I check that http client has 2 iddle connection
	And I check that http client has 2 total connection
	
Scenario: No connection is created when reach maximun client pool size
	When I create an http client "#client" with pool size 2
	And I post "/echo?delay=500" using client "#client" with body "Hey 1!" getting response "#response1"
	Then I check that http client "#client" has 1 active connection
	And I post "/echo?delay=500" using client "#client" with body "Hey 2!" getting response "#response2"
	And I check that http client "#client" has 2 active connection
	When I post "/echo?delay=500" using client "#client" with body "Hey 3!" getting response "#response3"
	Then I check that http client "#client" has 2 active connection
	And I check that "#response1" is success
	And I check that "#response2" is success
	And I check that "#response3" is success
	And I check that http response "#response1" has body "Hey 1!"
	And I check that http response "#response2" has body "Hey 2!"
	And I check that http response "#response3" has body "Hey 3!"
	And I check that http client "#client" has 2 iddle connection
	When I get "/get" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello GET!"
	And I check that http client "#client" has 2 iddle connection
	And I check that http client "#client" has 2 total connection
	
Scenario: Unpooled channel factory
	When I create an http client "#client" with unpooled channels
	And I post "/echo?delay=500" using client "#client" with body "Hey 1!" getting response "#response1"
	Then I check that http client "#client" has 1 active connection
	And I post "/echo?delay=500" using client "#client" with body "Hey 2!" getting response "#response2"
	And I check that http client "#client" has 2 active connection
	When I post "/echo?delay=500" using client "#client" with body "Hey 3!" getting response "#response3"
	And I check that http client "#client" has 3 active connection
	And I check that "#response1" is success
	And I check that "#response2" is success
	And I check that "#response3" is success
	And I check that http response "#response1" has body "Hey 1!"
	And I check that http response "#response2" has body "Hey 2!"
	And I check that http response "#response3" has body "Hey 3!"
	And I check that http client "#client" has 0 iddle connection
	When I get "/get" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello GET!"
	And I check that http client "#client" has 0 iddle connection
	And I check that http client "#client" has 0 total connection

Scenario: Response timeout
	When I post "/echo?delay=500" with body "Hey!" and response time 200 getting response "#response"
	Then I check that "#response" is failure
	And I check that error cause of "#response" is instancence of "io.netty.handler.timeout.ReadTimeoutException"
	When I post "/echo?delay=500" with body "Hey!" and response time 700 getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hey!"
	
Scenario: Channel pool idle timeout
	When I create an http client "#client" with pool idle timeout 300
	And I post "/echo?delay=500" with body "Hello" using http client "#client" getting response "#response"
	Then I check that client "#client" has 1 active connection
	And I check that client "#client" has 0 iddle connection
	And I check that "#response" is success
	And I check that http response "#response" has body "Hello"
	And I check that client "#client" has 0 active connection
	And I check that client "#client" has 1 iddle connection
	When I wait 350 milliseconds
	Then I check that client "#client" has 0 iddle connection
	
Scenario: Read timeout
	When I create an http client "#client" with read timeout 200
	And I post "/echo?delay=500" using client "#client" with body "Hey!" getting response "#response"
	Then I check that client "#client" has 1 active connection
	And I check that "#response" is failure
	And I check that error cause of "#response" is instancence of "io.netty.handler.timeout.ReadTimeoutException"
	And I check that client "#client" has 0 iddle connection

Scenario: Connection error
	When I get "/get" to port 9090 getting response "#response"
	Then I check that "#response" is failure
	And I check that error cause of "#response" contains message "Connection refused: localhost/127.0.0.1:9090"

Scenario: Single thread client
	Given a single thread event loop group "#eventLoopGroup"
	When I create an http client "#client" with event loop group "#eventLoopGroup"
	And I post "/echo?delay=500" using client "#client" with body "Hey 1!" getting response "#response1"
	And I post "/echo?delay=500" using client "#client" with body "Hey 2!" getting response "#response2"
	Then I check that "#response1" is success
	And I check that "#response2" is success
	And I check that http response "#response1" has body "Hey 1!"
	And I check that http response "#response2" has body "Hey 2!"
	And I check that http client "#client" has 2 iddle connection
	
Scenario: Connection closed
	When I get "/close" getting response "#response"
	Then I check that "#response" is failure
	And I check that error cause of "#response" is instancence of "java.nio.channels.ClosedChannelException"
	
Scenario: Client connections close
	When I get "/get" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello GET!"
	And I check that http client has 1 iddle connection
	When I close client connections "#closeFuture"
	Then I check that "#closeFuture" is success
	And I check that http client has 0 iddle connection