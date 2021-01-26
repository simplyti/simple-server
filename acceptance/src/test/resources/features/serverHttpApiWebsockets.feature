@websockets @standalone
Feature: WebSockets

Scenario: WebSocket service
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