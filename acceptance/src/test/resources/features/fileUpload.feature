Feature: File upload

Scenario: File server
	Given next file "#tempdir/testfiles/file1.txt" with content
	"""
	Hello!
	"""
	When I start a service "#serviceFuture" with API "com.simplyti.service.APIUploadTest"
	Then I check that "#serviceFuture" is success
	When I send files "#tempdir/testfiles/file1.txt" to "/upload" getting "#response"
	And I check that "#response" has status code 200
	And I check that "#response" is equals to "Got [file1.txt (6b)]"
	