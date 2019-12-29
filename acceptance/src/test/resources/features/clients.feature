@clients
Feature: Clients

@standalone
Scenario: Connection is reused when using pooled client
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/hello" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	And I check that http client has 1 iddle connection
	When I get "/hello" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	And I check that http client has 1 iddle connection
	
@standalone
Scenario: New connection is created if needed when use unlimited pooled client
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
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
	
@standalone
Scenario: No connection is created when reach maximun client pool size
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I create an http client "#client" with pool size 2
	When I post "/echo/delay?millis=500" using client "#client" with body "Hey 1!" getting response "#response1"
	And I check that http client "#client" has 1 active connection
	And I post "/echo/delay?millis=500" using client "#client" with body "Hey 2!" getting response "#response2"
	And I check that http client "#client" has 2 active connection
	And I post "/echo/delay?millis=500" using client "#client" with body "Hey 3!" getting response "#response3"
	And I check that http client "#client" has 2 active connection
	Then I check that "#response1" is success
	And I check that "#response2" is success
	And I check that "#response3" is success
	And I check that http response "#response1" has body "Hey 1!"
	And I check that http response "#response2" has body "Hey 2!"
	And I check that http response "#response3" has body "Hey 3!"
	And I check that http client "#client" has 2 iddle connection
	When I post "/hello" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	And I check that http client "#client" has 2 iddle connection
	And I check that http client "#client" has 2 total connection
	
@standalone
Scenario: Unpooled channel factory
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I create an http client "#client" with unpooled channels
	When I post "/echo/delay?millis=500" using client "#client" with body "Hey 1!" getting response "#response1"
	And I check that http client "#client" has 1 active connection
	And I post "/echo/delay?millis=500" using client "#client" with body "Hey 2!" getting response "#response2"
	And I check that http client "#client" has 2 active connection
	And I post "/echo/delay?millis=500" using client "#client" with body "Hey 3!" getting response "#response3"
	And I check that http client "#client" has 3 active connection
	Then I check that "#response1" is success
	And I check that "#response2" is success
	And I check that "#response3" is success
	And I check that http response "#response1" has body "Hey 1!"
	And I check that http response "#response2" has body "Hey 2!"
	And I check that http response "#response3" has body "Hey 3!"
	And I check that http client "#client" has 0 iddle connection
	When I post "/hello" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	And I check that http client "#client" has 0 iddle connection
	And I check that http client "#client" has 0 total connection

@standalone	
Scenario: Response timeout
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I post "/echo/delay?millis=500" with body "Hey!" and response time 200 getting response "#response"
	Then I check that "#response" is failure
	And I check that error cause of "#response" is instancence of "io.netty.handler.timeout.ReadTimeoutException"
	When I post "/echo/delay?millis=500" with body "Hey!" and response time 700 getting response "#response"
	And I check that "#response" is success
	And I check that http response "#response" has body "Hey!"
	
@standalone
Scenario: Channel pool idle timeout
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I create an generic client "#client" with pool idle timeout 300
	And I post "/echo/delay?millis=500" with body "Hello" using generic client "#client" getting response "#response"
	Then I check that client "#client" has 1 active connection
	And I check that client "#client" has 0 iddle connection
	And I check that "#response" is success
	And I check that client "#client" has 0 active connection
	And I check that client "#client" has 1 iddle connection
	When I wait 350 milliseconds
	Then I check that client "#client" has 0 iddle connection
	
@standalone	
Scenario: Read timeout
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I post "/echo/delay?millis=500" with body "Hey!" and read timeout 200 getting response "#response"
	Then I check that "#response" is failure
	And I check that error cause of "#response" is instancence of "io.netty.handler.timeout.ReadTimeoutException"
	When I post "/echo/delay?millis=500" with body "Hey!" and read timeout 700 getting response "#response"
	And I check that "#response" is success
	And I check that http response "#response" has body "Hey!"

@standalone	
Scenario: Connection error
	When I get "/hello" getting response "#response"
	And I check that "#response" is failure
	And I check that error cause of "#response" contains message "Connection refused: localhost/127.0.0.1:8080"

@standalone	
Scenario: Single thread client
	Given a single thread event loop group "#eventLoopGroup"
	When I create an http client "#client" with event loop group "#eventLoopGroup"
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/hello" using client "#client" in event loop "#eventLoopGroup" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	When I get "/hello" using client "#client" in event loop "#eventLoopGroup" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	And I check that http client "#client" has 1 iddle connection

@standalone	
Scenario: Connection write stream with http objecs
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I post "/echo" with body stream "#stream", content part "Hello ", length of 20 getting response "#response"
	Then I check that "#response" is not complete
	When I send "stream." to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	Then I check that "#response" is not complete
	When I send last content "The end" to http stream "#stream" getting "#writeresult"
	Then I check that "#writeresult" is success
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello stream.The end"
	And I check that http client has 1 iddle connection
	When I post "/echo" with body stream "#stream", content part "Bye", length of 4 getting response "#response"
	Then I check that "#response" is not complete
	When I send last content "!" to http stream "#stream" getting "#writeresult"
	Then I check that "#writeresult" is success
	Then I check that "#response" is success
	And I check that http response "#response" has body "Bye!"
	And I check that http client has 1 iddle connection
	
@standalone
Scenario: Single thread stream with http objects
	Given a single thread event loop group "#eventLoopGroup"
	When I create an http client "#client" with event loop group "#eventLoopGroup"
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I post "/echo" using client "#client" with body stream "#stream", content part "Hello " from loop group "#eventLoopGroup", length of 20 getting response "#response"
	Then I check that "#response" is not complete
	When I send "stream." to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	Then I check that "#response" is not complete
	When I send last content "The end" to http stream "#stream" getting "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#response" is success
	And I check that http response "#response" has body "Hello stream.The end"
	And I check that http client "#client" has 1 iddle connection
	When I post "/echo" using client "#client" with body stream "#stream", content part "Bye", length of 4 getting response "#response"
	Then I check that "#response" is not complete
	When I send last content "!" to http stream "#stream" getting "#writeresult"
	Then I check that "#writeresult" is success
	Then I check that "#response" is success
	And I check that http response "#response" has body "Bye!"
	And I check that http client "#client" has 1 iddle connection

@standalone
Scenario: Connection error when write stream
	When I post "/echo" with body stream "#stream", content part "Hello", length of 6 getting response "#response"
	And I check that "#response" is failure
	When I send "!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is failure
	And I check that error cause of "#writeresult" contains message "Connection refused: localhost/127.0.0.1:8080"
	Then I check that "#response" is failure
	And I check that error cause of "#writeresult" contains message "Connection refused: localhost/127.0.0.1:8080"

@standalone	
Scenario: Connection closed
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/close" getting response "#response"
	And I check that "#response" is failure
	And I check that "#response" has conention closed failure
	And I check that error cause of "#response" is instancence of "java.nio.channels.ClosedChannelException"
	
@standalone
Scenario: Client connections close
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/hello" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	And I check that http client has 1 iddle connection
	When I close client connections "#closeFuture"
	Then I check that "#closeFuture" is success
	And I check that http client has 0 iddle connection
	
Scenario Outline: Client using <type> proxy
	Given "<type>" proxy "<address>" as "#proxy"
	When I get url "http://httpbin:80/status/204" through proxy "#proxy" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 204
	And I check that http response "#response" has body ""
	Examples:
	| type 		| address 				|
	| SOCKS5	| 127.0.0.1:1080		|
	| HTTP		| 127.0.0.1:3128		|

Scenario: Client using proxy with authentication
	Given "SOCKS5" proxy "127.0.0.1:1081" as "#proxy"
	When I get url "http://httpbin:80/status/204" through proxy "#proxy" with username "proxyuser" and password "123456" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 204
	And I check that http response "#response" has body ""
	
	
