@websockets @standalone
Feature: Http client webSockets

Scenario: WebSocket client
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.WebsocketsApi"
	Then I check that "#serviceFuture" is success
	When I connect to websocket "#ws" with uri "/ws" getting text stream "#stream"
	Then I check that text stream "#stream" is equals to "Hello!"
	When I send message "Hello WS!" to websocket "#ws" getting "#writeFuture"
	Then I check that "#writeFuture" is success
	And I check that text stream "#stream" is equals to "Hello WS!"
	When I send message "Bye WS!" to websocket "#ws" getting "#writeFuture"
	Then I check that "#writeFuture" is success
	And I check that text stream "#stream" is equals to "Bye WS!"
	
Scenario: WebSocket client with inmediate sent
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.WebsocketsApi"
	Then I check that "#serviceFuture" is success
	When I connect to websocket "#ws" with uri "/ws" getting text objects stream "#objects"
	When I send message "Hello WS!" to websocket "#ws" getting "#writeFuture1"
	And I send message "Bye WS!" to websocket "#ws" getting "#writeFuture2"
	Then I check that "#writeFuture1" is success
	And I check that "#writeFuture2" is success
	And I check that object 0 in list "#objects" is equals to "Hello!"
	And I check that object 1 in list "#objects" is equals to "Hello WS!"
	And I check that object 2 in list "#objects" is equals to "Bye WS!"
	
Scenario: WebSocket client handle server close request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.WebsocketsApi"
	Then I check that "#serviceFuture" is success
	When I connect to websocket "#ws" with uri "/ws" getting text stream "#stream" and close future "#close"
	Then I check that text stream "#stream" is equals to "Hello!"
	When I send message "Hello WS!" to websocket "#ws" getting "#writeFuture"
	Then I check that "#writeFuture" is success
	And I check that text stream "#stream" is equals to "Hello WS!"
	When I send message "Sayonara baby" to websocket "#ws" getting "#writeFuture"
	Then I check that "#writeFuture" is success
	Then I check that "#close" is success
	
Scenario: WebSocket client error
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.WebsocketsApi"
	Then I check that "#serviceFuture" is success
	When I connect to websocket "#ws" with uri "/ws/notfound" getting clonnection future "#connect"
	And I send message "Hello WS!" to websocket "#ws" getting "#writeFuture"
	Then I check that "#writeFuture" is failure
	And I check that error cause of "#writeFuture" is instancence of "io.netty.handler.codec.http.websocketx.WebSocketClientHandshakeException"
	And I check that "#connect" is failure
	And I check that error cause of "#connect" is instancence of "io.netty.handler.codec.http.websocketx.WebSocketClientHandshakeException"
	When I send message "Hello WS!" to websocket "#ws" getting "#writeFuture"
	Then I check that "#writeFuture" is failure
	And I check that error cause of "#writeFuture" is instancence of "io.netty.handler.codec.http.websocketx.WebSocketClientHandshakeException"