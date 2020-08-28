@clients @proxy
Feature: Clients

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
	
	
