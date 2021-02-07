package devs.mrp.gullproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
				classes = AtributoServiceProxyWebClient.class)
@AutoConfigureWebTestClient
@EnableAutoConfiguration
@ActiveProfiles("test")
class AtributoServiceProxyWebClientTest {
	
	// https://www.paradigmadigital.com/dev/implementando-test-integracion-microservicios-spring-cloud-contract/
	
	@Autowired
	WebTestClient webTestClient;

	@Test
	void testValidateDataFormat() throws Exception {
		String type = "STRING";
		String data = "exampleData";
		String wrongType = "INTEGER";
		String response;
		
		response = webTestClient.
				get()
				.uri(uriBuilder -> uriBuilder
				.path("/atributo-service/api/atributos/data-validator")
				.queryParam("type", type)
				.queryParam("data", data)
				.build())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.returnResult(String.class)
				.getResponseBody()
				.blockFirst();
		assertThat(response).matches("true");
		
		response = webTestClient.
				get()
				.uri(uriBuilder -> uriBuilder
				.path("/atributo-service/api/atributos/data-validator")
				.queryParam("type", wrongType)
				.queryParam("data", data)
				.build())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.returnResult(String.class)
				.getResponseBody()
				.blockFirst();
		assertThat(response).matches("false");
	}

}
