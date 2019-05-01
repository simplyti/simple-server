Feature: Gateway with k8s discovery

Background:
	Given a namespace "acceptance"
	
Scenario: Simple Gateway
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an ingress in namespace "acceptance" with name "test" and backend service "theservice:80"
	Then I check that exist 1 gateway services

Scenario: Create service after ingress
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create an ingress in namespace "acceptance" with name "httpbin" and backend service "service:8080"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to ""
	
Scenario: Ingress with service port name reference
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create an ingress in namespace "acceptance" with name "httpbin" and backend service "service" with port name "http"
	And I create a service in namespace "acceptance" with name "service" with port 8080, name "http" to target 8081
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to ""
	
Scenario: Create ingress after service
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	And I create an ingress in namespace "acceptance" with name "httpbin" and backend service "service:8080"
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to ""
	

Scenario: Create endpoint after service
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an ingress in namespace "acceptance" with name "httpbin" and backend service "service:8080"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	And I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to ""

Scenario: Path specific gateway
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	And I create an ingress in namespace "acceptance" with name "httpbin", path "/status" and backend service "service:8080"
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to ""
	When I send a "GET /status/badcode" getting "#response"
	Then I check that "#response" has status code 400
	And I check that "#response" is equals to "Invalid status code"

Scenario: Not found service
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	And I create an ingress in namespace "acceptance" with name "httpbin", path "/status" and backend service "service:8080"
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I send a "GET /other" getting "#response"
	Then I check that "#response" has status code 404
	
Scenario: Found service without endpoints
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	And I create an ingress in namespace "acceptance" with name "httpbin", path "/status" and backend service "service:8080"
	Then I check that exist 1 gateway services
	When I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 503
	
Scenario: Delete ingress
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	And I create an ingress in namespace "acceptance" with name "httpbin", path "/status" and backend service "service:8080"
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I delete ingress "httpbin" in namespace "acceptance"
	Then I check that exist 0 gateway services
	When I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 404
	
Scenario: Delete endpoint
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	And I create an ingress in namespace "acceptance" with name "httpbin", path "/status" and backend service "service:8080"
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I delete endpoint "service" in namespace "acceptance"
	Then I check that exist a gateway service without targets
	
Scenario: Delete service
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	And I create an ingress in namespace "acceptance" with name "httpbin", path "/status" and backend service "service:8080"
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I delete service "service" in namespace "acceptance"
	Then I check that exist a gateway service without targets

Scenario: Modify endpoint
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	And I create an ingress in namespace "acceptance" with name "httpbin", path "/status" and backend service "service:8080"
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I update endpoint "service" in namespace "acceptance" adding addresses "10.0.0.1:8081"
	Then I check that exist a gateway service with targets "http://${local.address}:8081,http://10.0.0.1:8081"
	When I update endpoint "service" in namespace "acceptance" setting addresses "${local.address}:8081"
	Then I check that exist a gateway service with targets "http://${local.address}:8081"

Scenario: Modify service
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create a service in namespace "acceptance" with name "service" with port 80 to target 8081
	And I create an ingress in namespace "acceptance" with name "httpbin", path "/status" and backend service "service:8080"
	Then I check that exist 1 gateway services
	And I check that exist a gateway service without targets
	When I update service "service" in namespace "acceptance" setting port 8080 and target port 8081
	Then I check that exist a gateway service with targets "http://${local.address}:8081"
	
Scenario: Modify ingress
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	And I create an ingress in namespace "acceptance" with name "httpbin", path "/status" and backend service "service:8080"
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to ""
	When I update ingress "httpbin" in namespace "acceptance" adding path "/base64" and backend "service:8080"
	Then I check that exist 2 gateway services
	When I send a "GET /base64/SGVsbG8gR2F0ZXdheQ==" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello Gateway"

Scenario: Secure backend
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:2223"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 2223
	And I create an ingress in namespace "acceptance" with name "httpbin", path "/status", backend service "service:8080" and annotations
		| ingress.kubernetes.io/secure-backends | true |
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "https://${local.address}:2223"
	When I send a "GET /status/200" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to ""
	
Scenario: Ingress TLS
	Given a key pair "#keypair" with algorithm "RSA" and bits 1024
    And a certificate "#cert" autosigned with key "#keypair" with common name "httpbin.org"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a server certificate secret in namespace "acceptance" with name "httpbin-tls", key "#keypair" and cert "#cert"
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	And I create an ingress in namespace "acceptance" with name "httpbin", host "httpbin.local", tls secret "httpbin-tls" and backend service "service:8080"
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I send a "GET /status/200" using ssl port 8443 with sni "httpbin.local" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to ""
	And I check that server certificate has name "CN=httpbin.org"
	
Scenario: Delete TLS secret
	Given a key pair "#keypair" with algorithm "RSA" and bits 1024
    And a certificate "#cert" autosigned with key "#keypair" with common name "httpbin.org"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create a server certificate secret in namespace "acceptance" with name "httpbin-tls", key "#keypair" and cert "#cert"
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	And I create an ingress in namespace "acceptance" with name "httpbin", host "httpbin.local", tls secret "httpbin-tls" and backend service "service:8080"
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I send a "GET /status/200" using ssl port 8443 with sni "httpbin.local" getting "#response"
	Then I check that "#response" has status code 200
	And I check that server certificate has name "CN=httpbin.org"
	When I close all client connections
	When I delete secret "httpbin-tls" in namespace "acceptance"
	And I wait 100 milliseconds
	And I send a "GET /status/200" using ssl port 8443 with sni "httpbin.local" getting "#response"
	Then I check that "#response" has status code 200
	And I check that server certificate has name "CN=Simple Server"
	
Scenario: OIDC auth ingress
	Given a selfsigned certificate "#certificate"
    And a JWT sign key "#key" from self signed ccertificate "#certificate"
    And an openid provider listening in port 7443 with sign certificate "#certificate", authorization endpoint "/authorize" and token endpoint "/token"
	When I create a secret in namespace "acceptance" with name "auth0-client" and next data:
		| clientId | gwDxKuNUjC8U4gKwmClumsRxFQLRLHTI |
		| clientSecret | d8xh5dw4mnoo6xaYJfMhwNbazhmKXrw21GvBJUlovfGP2PKO_Vc0BdsWmW5qq5sW |
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(/_api/callback,myCipherKey,http://localhost:8082) |
		| withLog4J2Logger		|		|
	Then I check that "#serviceFuture" is success
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	And I create an ingress in namespace "acceptance" with name "httpbin", path "/status", backend service "service:8080" and annotations
		| ingress.kubernetes.io/auth-type | oidc |
		| ingress.kubernetes.io/auth-realm | https://localhost:7443 |
		| ingress.kubernetes.io/auth-secret | auth0-client |
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I send a "GET /status/200" getting "#response" with status code 302 eventually
	Then I check that "#response" has status code 302 with location starting with "https://localhost:7443/authorize"
	And I check that "#response" redirect location contains params:
		| redirect_uri		| https://localhost/_api/callback	|
		| response_type		| code | 
		| client_id			| gwDxKuNUjC8U4gKwmClumsRxFQLRLHTI	|
		| scope				| openid email profile groups		|
		| approval_prompt	| force 								|
		| access_type		| offline							|
	When I send a "GET /_api/callback" following auth redirect of "#response" getting "#response"
	Then I check that "#response" has status code 302 
	And I check that "#response" has location header "https://localhost/status/200"
	And I check that "#response" has cookie "JWT-SESSION"
	When I send a "GET /status/200" with cookies from response "#response" getting "#response"
	Then I check that "#response" has status code 200	
	
Scenario: Ingress rewrite
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withModule			| com.simplyti.service.gateway.GatewayModule |
		| withModule			| com.simplyti.service.discovery.k8s.KubernetesServiceDiscoveryModule(http://localhost:8082) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I create an endpoint in namespace "acceptance" with name "service" and address "${local.address}:8081"
	And I create an ingress in namespace "acceptance" with name "httpbin", path "/httpbinstatus", backend service "service:8080" and annotations
		| ingress.kubernetes.io/rewrite-target | /status |
	And I create a service in namespace "acceptance" with name "service" with port 8080 to target 8081
	Then I check that exist 1 gateway services
	And I check that exist a gateway service with targets "http://${local.address}:8081"
	When I send a "GET /httpbinstatus/200" getting "#response"
	Then I check that "#response" has status code 200
