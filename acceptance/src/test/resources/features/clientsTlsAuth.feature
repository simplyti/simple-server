@standalone @clients @tls
Feature: Clients TLS Authentication

Scenario: Connection TLS authentication
  Given a key pair "#keypair" with algorithm "RSA" and bits 1024
  And a certificate "#cert" autosigned with key "#keypair" with common name "my-client"
  And an ssl endpoint "#endpoint" for "https://localhost:8443" using certificate "#cert" and private key from key pair "#keypair"
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/hello/tlsname" form endpoint "#endpoint" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello my-client!"