@gateway @standalone
Feature: Gateway

Scenario: Simple gateway
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest	|
		| listener		| 4444	|
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayeFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayeFuture" is success
	When I create a service with path "/hello" and backend "http://127.0.0.1:4444"
	And I send a "GET /hello" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello!"

Scenario: Simple gateway with body
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest	|
		| listener		| 4444	|
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayeFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayeFuture" is success
	When I create a service with path "/echo" and backend "http://127.0.0.1:4444"
	When I send a "POST /echo" with body "Hello super gateway!" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello super gateway!"
	
Scenario: Gateway can serve its own API
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest	|
		| listener		| 4444	|
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayeFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayeFuture" is success
	When I create a service with path "/hello" and backend "http://127.0.0.1:4444"
	And I send a "GET /gateway" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello from gtw!"
	And I send a "GET /hello" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello!"
	When I send a "GET /gateway" getting "#response"
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello from gtw!"

Scenario: Gateway reuse upstream connections
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest	|
		| listener		| 4444	|
	Then I check that "#serviceFuture" is success
	Given gateway config "#config" with monitor enabled
	When I start a service "#gatewayeFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule(#config) |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayeFuture" is success
	When I create a service with path "/hello" and backend "http://127.0.0.1:4444"
	And I send a "GET /hello" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello!"
	And I check that gateway "#gatewayeFuture" client has 1 iddle connection
	And I send a "GET /hello" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello!"
	And I check that gateway "#gatewayeFuture" client has 1 iddle connection
	And I send 1000 serialized request "GET /hello" getting response error ratio "#errors"
	Then I check that error ratio "#errors" is 0.0
	And I check that gateway "#gatewayeFuture" client has 1 iddle connection
	
Scenario: Gateway can handle concurrent requests by increasing upstream connections
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest	|
		| listener		| 4444	|
	Then I check that "#serviceFuture" is success
	Given gateway config "#config" with monitor enabled
	When I start a service "#gatewayeFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule(#config) |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayeFuture" is success
	When I create a service with path "/echo" and backend "http://127.0.0.1:4444"
	When I post "/echo/delay?millis=500" with body "Hey 1!" getting response "#response1"
	And I post "/echo/delay?millis=500" with body "Hey 2!" getting response "#response2"
	And I check that gateway "#gatewayeFuture" client has 2 active connection
	Then I check that "#response1" is success
	And I check that "#response2" is success
	And I check that http response "#response1" has body "Hey 1!"
	And I check that http response "#response2" has body "Hey 2!"
	And I check that gateway "#gatewayeFuture" client has 2 iddle connection
	And I send 1000 parallel request "POST /echo" with body "Hello!" getting response error ratio "#errors"
	Then I check that error ratio "#errors" is 0.0
	And I check that gateway "#gatewayeFuture" client has multiple iddle connections
	
Scenario: Path based gateway routing
	When I start a service "#serviceFuture1" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.EchoApi1 |
		| listener			| 9091	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture1" is success
	When I start a service "#serviceFuture2" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.EchoApi2 |
		| listener			| 9092	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture2" is success
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	When I create a service with path "/put1" and backend "http://127.0.0.1:9091"
	And I create a service with path "/put2" and backend "http://127.0.0.1:9092"
	And I send a "PUT /put1" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello PUT 1"
	When I send a "PUT /put2" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello PUT 2"
	
Scenario: Not found service gateway
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /notfound" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 404
	And I check that http response "#response" has body ""
	
Scenario: Not available service gateway
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a service with path "/status"
	And I send a "GET /status/200" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 503
	And I check that http response "#response" has body ""

Scenario: Bad gateway service
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a service with path "/status" and backend "http://127.0.0.1:9090"
	And I send a "GET /status/200" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 502
	And I check that http response "#response" has body ""
	
Scenario: Simple gateway client closing connection can cause request errors
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest	|
		| listener		| 4444	|
	Then I check that "#serviceFuture" is success
	And I start a service "#gateway" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gateway" is success
	When I create a service with path "/hello" and backend "http://127.0.0.1:4444"
	And I send 1000 serialized request "GET /hello/close" getting response error ratio "#errors"
	Then I check that error ratio "#errors" less than 0.005
	And I send 1000 parallel request "GET /hello/close" getting response error ratio "#errors"
	Then I check that error ratio "#errors" less than 0.1
	And I send 1000 parallel request "GET /hello/close?delay=30" getting response error ratio "#errors"
	Then I check that error ratio "#errors" less than 0.2
	
Scenario: Service method matching
	When I start a service "#serviceFuture1" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.EchoApi1 |
		| listener			| 9091	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture1" is success
	When I start a service "#serviceFuture2" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.EchoApi2 |
		| listener			| 9092	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture2" is success
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	And I create a service with method "POST" with path "/echo" and backend "http://127.0.0.1:9091"
	And I create a service with method "GET" with path "/echo" and backend "http://127.0.0.1:9092"
	When I send a "POST /echo" with body "Hello echo" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello echo"
	And I send a "GET /echo" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello GET"
	
Scenario: Especific method service is priorized
	When I start a service "#serviceFuture1" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.EchoApi1 |
		| listener			| 9091	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture1" is success
	When I start a service "#serviceFuture2" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.EchoApi2 |
		| listener			| 9092	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture2" is success
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	When I create a service with path "/hello" and backend "http://127.0.0.1:9091"
	And I create a service with method "GET" with path "/hello" and backend "http://127.0.0.1:9092"
	And I send a "GET /hello" getting "#response"
  Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello from service get"
	When I send a "POST /hello" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello from service post"

Scenario: Especific host service is priorized
	When I start a service "#serviceFuture1" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.EchoApi1 |
		| listener			| 9091	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture1" is success
	When I start a service "#serviceFuture2" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.EchoApi2 |
		| listener			| 9092	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture2" is success
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	When I create a service with backend "http://127.0.0.1:9091"
	And I create a service with host "service.local" and backend "http://127.0.0.1:9092"
	And I send a "GET /hello" with header "host" with value "service.local" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello from service get"
	
Scenario: Host service match only when host header is present
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest	|
		| listener		| 4444	|
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	When I create a service with host "service.local" with path "/hello" and backend "http://127.0.0.1:4444"
	And I send a "GET /hello" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 404
	And I check that http response "#response" has body ""
	When I send a "GET /hello" with header "host" with value "service.local" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello!"
	
Scenario: Path template based service
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest	|
		| listener		| 4444	|
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	When I create a service with path "/responsecode/{code:\d+}" and backend "http://127.0.0.1:4444"
	And I send a "GET /responsecode/200" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body ""
	When I send a "GET /responsecode/baad" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 404
	And I check that http response "#response" has body ""
	
Scenario: Service path rewrite based on path template service
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest	|
		| listener		| 4444	|
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	When I create a service with path "/{code:\d+}", rewrite "/responsecode" and backend "http://127.0.0.1:4444"
	And I send a "GET /200" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body ""
	
Scenario: Prematurely failed request discards nexts http parts
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest |
		| listener			| 9090	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	And I post "/echo" with chunked body stream "#stream" getting response "#response"
	Then I check that "#response" is failure
	Then I check that http error of "#response" contains status code 404
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream"
	Then I check that "#writeresult" is success
	And I post "/gateway/echo" with chunked body stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello stream!"
	
	
# TODO: check proxy req adapt

Scenario: Http Continue handle
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.APITest	|
		| listener		| 4444	|
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayeFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayeFuture" is success
	When I create a service with path "/echo" and backend "http://127.0.0.1:4444"
	When I create a service with path "/hello" and backend "http://127.0.0.1:4444"
	And I post "/echo" with 100 bytes random body an continue expected getting "#response"
	Then I check that http response "#response" has status code 200
	And I check that response "#response" has body size 100
	When I send a "GET /hello" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello!"

Scenario: Handling big payloads with a gateway service and an own API
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest |
		| listener			| 9090	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	When I create a service with path "/echo" and backend "http://127.0.0.1:9090"
	When I create a service with path "/bad" and backend "http://127.0.0.1:4444"
	And I post "/echo" with 5.0 mb of payload getting response "#response"
	Then I check that http response "#response" has status code 200
	And I check that http response "#response" has a body with size of 5.0 mb
	And I post "/gateway/echo" with 5.0 mb of payload getting response "#response"
	Then I check that http response "#response" has status code 200
	And I check that http response "#response" has a body with size of 5.0 mb
	And I post "/bad" with 1.0 mb of payload getting response "#response"
	Then I check that "#response" is failure
	Then I check that http error of "#response" contains status code 502
	And I post "/gateway/echo" with 5.0 mb of payload getting response "#response"
	Then I check that http response "#response" has status code 200
	And I check that http response "#response" has a body with size of 5.0 mb

Scenario: Websocket backend
	When I start a service "#serviceFuture" with options:
		| option	 	| value |
		| withApi		| com.simplyti.service.examples.api.WebsocketsApi	|
		| listener		| 4444	|
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	When I create a service with backend "http://127.0.0.1:4444"
	When I connect to websocket "#ws" with uri "/ws" getting text stream "#stream"
	Then I check that text stream "#stream" is equals to "Hello!"
	When I send message "Hello WS!" to websocket "#ws" getting "#writeFuture"
	Then I check that "#writeFuture" is success
	And I check that text stream "#stream" is equals to "Hello WS!"
	When I send message "Bye WS!" to websocket "#ws" getting "#writeFuture"
	Then I check that "#writeFuture" is success
	And I check that text stream "#stream" is equals to "Bye WS!"

Scenario: Websocket service connection error
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a service with backend "http://127.0.0.1:9090"
	When I connect to websocket "#ws" with uri "/" getting text stream "#stream"
	When I send message "Hello WS!" to websocket "#ws" getting "#writeFuture"
	Then I check that "#writeFuture" is failure
