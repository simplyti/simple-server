Feature: Gateway

Scenario: Simple gateway
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a service with path "/base64" and backend "http://127.0.0.1:8081"
	And I send a "GET /base64/SGVsbG8gR2F0ZXdheQ==" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello Gateway"

Scenario: Not found service
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /notfound" getting "#response"
	Then I check that "#response" has status code 404
	And I check that "#response" is equals to ""
	
Scenario: Not available service
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a service with path "/status"
	And I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 503
	And I check that "#response" is equals to ""
	
Scenario: Bad gateway service
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a service with path "/status" and backend "http://127.0.0.1:9090"
	And I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 502
	And I check that "#response" is equals to ""
	
Scenario: Streamed body post
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest |
		| insecuredPort		| 9090	|
		| securedPort		| 9091	|
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	When I create a service with path "/echo" and backend "http://127.0.0.1:9090"
	And I post "/echo" with body stream "#stream", content part "Hello ", length of 20 getting response objects "#response"
	Then I check that stream "#stream" is not complete
	When I send "stream." to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that stream "#stream" is not complete
	When I send "The end" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that stream "#stream" is success
	And I check that response stream "#response" contains body "Hello stream.The end"
	
Scenario: Service method match
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest |
		| insecuredPort		| 9090	|
		| securedPort		| 9091	|
		| withLog4J2Logger	|		|
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	When I create a service with method "GET" with path "/anything" and backend "http://127.0.0.1:8081"
	And I create a service with method "GET" with path "/status" and backend "http://127.0.0.1:8081"
	And I create a service with method "POST" with path "/echo" and backend "http://127.0.0.1:9090"
	And I send a "GET /anything" getting "#response"
	Then I check that "#response" has status code 200
	When I send a "GET /status/302" getting "#response"
	Then I check that "#response" has status code 302
	When I send a "POST /echo" with body "Hello echo" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello echo"
	
Scenario: Especific method is prioritized
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest |
		| insecuredPort		| 9090	|
		| securedPort		| 9091	|
		| withLog4J2Logger	|		|
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	When I create a service with path "/anything" and backend "http://127.0.0.1:8081"
	And I create a service with method "GET" with path "/anything" and backend "http://127.0.0.1:9090"
	And I send a "GET /anything" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to "This is a prioritized response"
	When I send a "POST /anything" getting "#response"
	Then I check that "#response" has status code 200
	
Scenario: Especific host is prioritized
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest |
		| insecuredPort		| 9090	|
		| securedPort		| 9091	|
		| withLog4J2Logger	|		|
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	When I create a service with backend "http://127.0.0.1:8081"
	And I create a service with host "service.local" and backend "http://127.0.0.1:9090"
	And I send a "GET /anything" with header "host" with value "service.local" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to "This is a prioritized response"
	
Scenario: Host service match only when host header present
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a service with host "httpbin.docker" with path "/status" and backend "http://127.0.0.1:8081"
	And I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 404
	When I send a "GET /status/200" with header "host" with value "httpbin.docker" getting "#response"
	Then I check that "#response" has status code 200
	
Scenario: Path template
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a service with path "/status/{code:\d+}" and backend "http://127.0.0.1:8081"
	And I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 200
	When I send a "GET /status/baad" getting "#response"
	Then I check that "#response" has status code 404
	
Scenario: Path template
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a service with path "/{code:\d+}", rewrite "/status" and backend "http://127.0.0.1:8081"
	And I send a "GET /200" getting "#response"
	Then I check that "#response" has status code 200

Scenario: Web socket backend
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a service with backend "http://127.0.0.1:8010"
	When I connect to websocket "#ws" getting text stream "#stream"
	Then I check that text stream "#stream" content match with "Request served by .*"
	When I send message "Hello WS!" to websocket "#ws" getting "#writeFuture"
	Then I check that "#writeFuture" is success
	And I check that text stream "#stream" is equals to "Hello WS!"
	When I send message "Bye WS!" to websocket "#ws" getting "#writeFuture"
	Then I check that "#writeFuture" is success
	And I check that text stream "#stream" is equals to "Bye WS!"

Scenario: Web socket service connection error
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a service with backend "http://127.0.0.1:9090"
	When I connect to websocket "#ws" getting text stream "#stream"
	When I send message "Hello WS!" to websocket "#ws" getting "#writeFuture"
	Then I check that "#writeFuture" is failure

Scenario: Web socket service bad handshake
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a service with backend "http://127.0.0.1:8081"
	When I connect to websocket "#ws" getting text stream "#stream"
	When I send message "Hello WS!" to websocket "#ws" getting "#writeFuture"
	Then I check that "#writeFuture" is failure
	
Scenario: Path based routing
	When I start a service "#serviceFuture" with options:
		| option	 		| value |
		| withApi			| com.simplyti.service.examples.api.APITest |
		| insecuredPort		| 9090	|
		| securedPort		| 9091	|
		| withLog4J2Logger	|		|
	When I start a service "#gatewayFuture" with options:
		| option	 		| value |
		| withModule		| com.simplyti.service.gateway.GatewayModule |
		| withModule		| com.simplyti.service.discovery.TestServiceDiscoveryModule |
		| withLog4J2Logger	|		|
	Then I check that "#gatewayFuture" is success
	When I create a service with path "/status" and backend "http://127.0.0.1:8081"
	And I create a service with path "/responsecode" and backend "http://127.0.0.1:9090"
	And I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 200
	When I send a "GET /responsecode/204" getting "#response"
	Then I check that "#response" has status code 204
		