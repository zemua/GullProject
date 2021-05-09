import org.springframework.cloud.contract.spec.Contract

// ./mvnw clean test -e -X
// ./mvnw clean install

[
 Contract.make {
	 name("should validate string description true")
	 // You can give a description of the use case that the contract represents
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
	 // You can give a description of the use case that the contract represents
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
	 // You can give a description of the use case that the contract represents
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
 },
   Contract.make {
	 name("should get atributo object by its id")
	 // You can give a description of the use case that the contract represents
	 description('''
		 given:
		 Some id
		 when:
		 It matches an Atributo object in the db
		 then:
		 The object is returned
	 ''')
	 request {
		 method 'GET'
		 url $(c(regex('/api/atributos/idforcampo/([a-zA-Z0-9]+)')), p('/api/atributos/idforcampo/unID123'))
		 headers {
			 contentType('text/html')
		 }
	 }
	 // https://cloud.spring.io/spring-cloud-contract/1.2.x/multi/multi__contract_dsl.html#contract-matchers
	 response {
		 status 200
		 body ([
			 id: $(c('unID123'), p(regex('[a-zA-Z0-9]+'))),
			 name: $(c('response name'), p(regex(nonBlank()))),
			 tipo: $(c('RESPONSETYPE'), p(regex('[A-Z]+'))),
			 //valoresFijos: $(c('false'), p(regex(anyBoolean()))),
			 //links: [
			 //	rel: "self",
			 //	href: "/api/esto/es/un/link"
			 //]
		 ])
		 headers {
			 contentType('application/json')
		 }
	 }
 },
    Contract.make {
	 name("should get all attributos")
	 // You can give a description of the use case that the contract represents
	 description('''
		 given:
		 Any atributes in the database
		 when:
		 A request is made
		 then:
		 All are returned in a Flux
	 ''')
	 request {
		 method 'GET'
		 urlPath ('/api/atributos/all')
		 headers {
			 contentType('text/html')
		 }
	 }
	 response {
		 status 200
		 body ([
		 	[
				id: $(c('unID111'), p(regex('[a-zA-Z0-9]+'))),
				name: $(c('response name one'), p(regex(nonBlank()))),
				tipo: $(c('RESPONSETYPEONE'), p(regex('[A-Z]+'))),
				valoresFijos: $(c('false'), p(regex(anyBoolean()))),
				links: [
					rel: "self",
					href: $(c("/api/esto/es/un/link"), p(regex('^[a-zA-Z0-9/-]+$')))
				]
			],
			[
				id: $(c('unID222'), p(regex('[a-zA-Z0-9]+'))),
				name: $(c('response name two'), p(regex(nonBlank()))),
				tipo: $(c('RESPONSETYPETWO'), p(regex('[A-Z]+'))),
				valoresFijos: $(c('true'), p(regex(anyBoolean()))),
				links: [
					rel: "self",
					href: $(c("/api/esto/es/dos/link"), p(regex('^[a-zA-Z0-9/-]+$')))
				]
			]
		 ])
		 headers {
			 contentType('application/json')
		 }
	 }
 },
     Contract.make {
	 name("should get all formats")
	 // You can give a description of the use case that the contract represents
	 description('''
		 given:
		 The formats specified in DataFormat
		 when:
		 A request is made
		 then:
		 All are returned in a list of strings
	 ''')
	 request {
		 method 'GET'
		 urlPath ('/api/atributos/formatos')
		 headers {
			 contentType('text/html')
		 }
	 }
	 response {
		 status 200
		 body ([
		 	[string: 'DESCRIPCION'],
		 	[string: 'CANTIDAD'],
		 	[string: 'COSTE'],
		 	[string: 'MARGEN'],
		 	[string: 'PVP'],
		 	[string: 'PLAZO']
		 ])
		 headers {
			 contentType('application/json')
		 }
	 }
 },
     Contract.make {
	 name("should get string class type of description format")
	 // You can give a description of the use case that the contract represents
	 description('''
		 given:
		 The formats specified in DataFormat
		 when:
		 A request is made against an existing DataFormat
		 then:
		 Class type is returned
	 ''')
	 request {
		 method 'GET'
		 //urlPath $(c(regex('/api/atributos/typeofformat/([A-Z]+)')), p('/api/atributos/typeofformat/DESCRIPCION'))
		 urlPath ('/api/atributos/typeofformat/DESCRIPCION')
		 headers {
			 contentType('text/html')
		 }
	 }
	 response {
		 status 200
		 body (
		 	$(p('String'))
		 )
		 headers {
			 contentType('application/json')
		 }
	 }
 },
     Contract.make {
	 name("should get integer class type of cantidad format")
	 // You can give a description of the use case that the contract represents
	 description('''
		 given:
		 The formats specified in DataFormat
		 when:
		 A request is made against an existing DataFormat
		 then:
		 Class type is returned
	 ''')
	 request {
		 method 'GET'
		 //urlPath $(c(regex('/api/atributos/typeofformat/([A-Z]+)')), p('/api/atributos/typeofformat/CANTIDAD'))
		 urlPath ('/api/atributos/typeofformat/CANTIDAD')
		 headers {
			 contentType('text/html')
		 }
	 }
	 response {
		 status 200
		 body (
		 	$(p('Integer'))
		 )
		 headers {
			 contentType('application/json')
		 }
	 }
 },
     Contract.make {
	 name("should get a flux of all the attributes corresponding to the list of ids")
	 // You can give a description of the use case that the contract represents
	 description('''
		 given:
		 The list of ids provided
		 when:
		 Some ids exist in the database
		 then:
		 A flux is returned with those
	 ''')
	 request {
		 method 'GET'
		 urlPath $(c(regex('/api/atributos/arrayofcampos/byids\\?ids=((\\w+(\\,\\w+)*)?$)')), p('/api/atributos/arrayofcampos/byids?ids=5fd52181c3bd1d49e616e3be,5fd5ec64916371511cdd7c0e,pr1m3r0,s3g7nd0,t3rc3r0'))
		 headers {
			 contentType('text/html')
		 }
	 }
	 response {
		 status 200
		 body ([
		 	[
				id: $(c('s3g7nd0'), p(regex('[a-zA-Z0-9]+'))),
				name: $(c('response name two'), p(regex(nonBlank()))),
				tipo: $(c('RESPONSETYPETWO'), p(regex('[A-Z]+'))),
			],
			[
				id: $(c('t3rc3r0'), p(regex('[a-zA-Z0-9]+'))),
				name: $(c('response name three'), p(regex(nonBlank()))),
				tipo: $(c('RESPONSETYPETHREE'), p(regex('[A-Z]+'))),
			]
		 ])
		 headers {
			 contentType('application/json')
		 }
	 }
 }
]