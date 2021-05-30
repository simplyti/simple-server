@clients @proxy @standalone
Feature: Clients proxy

Background: Start server
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIExample"
	Then I check that "#serviceFuture" is success

Scenario Outline: Client using <type> proxy
	Given "<type>" proxy "<address>" as "#proxy"
	When I get url "http://localhost:8080/status/204" through proxy "#proxy" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 204
	And I check that http response "#response" has body ""
	Examples:
	| type 		| address 					|
	| SOCKS5	| 127.0.0.1:1080		|
	| SOCKS4	| 127.0.0.1:1080		|
	| HTTP		| 127.0.0.1:3128		|
	
Scenario Outline: Client using proxy with authentication success
	Given "<type>" proxy "<address>" as "#proxy"
	When I get url "http://localhost:8080/status/204" through proxy "#proxy" with username "proxyuser" and password "123456" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 204
	And I check that http response "#response" has body ""
	Examples:
	| type 		| address 					|
	| SOCKS5	| 127.0.0.1:1081		|
	| SOCKS4	| 127.0.0.1:1081		|
	| HTTP		| 127.0.0.1:3129		|
	
Scenario Outline: Client using proxy with authentication faiure
	Given "<type>" proxy "<address>" as "#proxy"
	When I get url "http://localhost:8080/responsecode/204" through proxy "#proxy" with username "baduser" and password "badpass" getting response "#response"
	Then I check that "#response" is failure
	Examples:
	| type 		| address 					|
	| SOCKS5	| 127.0.0.1:1081		|
	| SOCKS4	| 127.0.0.1:1081		|
	| HTTP		| 127.0.0.1:3129		|
	
