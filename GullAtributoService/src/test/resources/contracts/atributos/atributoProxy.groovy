import org.springframework.cloud.contract.spec.Contract
[
 Contract.make {
	 name("should validate string description true")
	 // You can give a description of the user case that the contract represents
	 description('''
		 given:
		 Some data
		 when:
		 The data and type are verified and correct
		 then:
		 A boolean true is returned, or false otherwise
	 ''')
	 request {
		 method 'GET'
		 urlPath('/api/atributos/data-validator') {
		 	queryParameters{
		 		//parameter 'type': $(c(regex("[a-zA-Z]+")), p('abcd'))
		 		//parameter 'data': $(c(regex(nonBlank())), p('abcd123, asdfg.'))
		 		parameter 'type': $(c('DESCRIPCION'), p('DESCRIPCION'))
		 		parameter 'data': $(c(regex(nonBlank())), p('correct data'))
		 	}
		 }
		 headers {
			 contentType('text/html')
		 }
	 }
	 response {
		 status 200
		 body (
			 $(p('true'))
		 )
		 headers {
			 contentType('application/json')
		 }
	 }
},
  Contract.make {
	 name("should validate cantidad integer true")
	 // You can give a description of the user case that the contract represents
	 description('''
		 given:
		 Some data
		 when:
		 The data and type are verified and correct
		 then:
		 A boolean true is returned, or false otherwise
	 ''')
	 request {
		 method 'GET'
		 urlPath('/api/atributos/data-validator') {
		 	queryParameters{
		 		//parameter 'type': $(c(regex("[a-zA-Z]+")), p('abcd'))
		 		//parameter 'data': $(c(regex(nonBlank())), p('abcd123, asdfg.'))
		 		parameter 'type': $(c('CANTIDAD'), p('CANTIDAD'))
		 		parameter 'data': $(c(regex(number())), p('123456'))
		 	}
		 }
		 headers {
			 contentType('text/html')
		 }
	 }
	 response {
		 status 200
		 body (
			 $(p('true'))
		 )
		 headers {
			 contentType('application/json')
		 }
	 }
 },
  Contract.make {
	 name("should validate cantidad integer false")
	 // You can give a description of the user case that the contract represents
	 description('''
		 given:
		 Some data
		 when:
		 The data and type are verified and correct
		 then:
		 A boolean true is returned, or false otherwise
	 ''')
	 request {
		 method 'GET'
		 urlPath('/api/atributos/data-validator') {
		 	queryParameters{
		 		//parameter 'type': $(c(regex("[a-zA-Z]+")), p('abcd'))
		 		//parameter 'data': $(c(regex(nonBlank())), p('abcd123, asdfg.'))
		 		parameter 'type': $(c('CANTIDAD'), p('CANTIDAD'))
		 		parameter 'data': $(c(regex("[a-zA-Z]+")), p('azsxDCFV'))
		 	}
		 }
		 headers {
			 contentType('text/html')
		 }
	 }
	 response {
		 status 200
		 body (
			 $(p('false'))
		 )
		 headers {
			 contentType('application/json')
		 }
	 }
 }
]