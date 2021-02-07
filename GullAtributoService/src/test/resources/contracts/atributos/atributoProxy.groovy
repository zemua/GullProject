import org.springframework.cloud.contract.spec.Contract
[
 Contract.make {
	 name("should validate format")
	 // You can give a description of the user case that the contract represents
	 description('''
		 given:
		 Some data
		 when:
		 The data and type is verified
		 then:
		 A boolean true is returned
	 ''')
	 request {
		 method 'GET'
		 urlPath('/api/atributos/data-validator') {
		 	queryParameters{
		 		parameter 'type': $(c(regex("[a-zA-Z]+")), p('abcd'))
		 		parameter 'data': $(c(regex(nonBlank())), p('abcd123, asdfg.'))
		 	}
		 }
		 headers {
			 contentType('text/html')
		 }
	 }
	 response {
		 status 200
		 body (
			 $(p(regex("true|false")))
		 )
		 headers {
			 contentType('application/json')
		 }
	 }
 }
]