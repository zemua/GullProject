package devs.mrp.gullproject.rest;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = DataFormatValidatorApi.class)
class DataFormatValidatorApiTest {
	
	@Autowired
	DataFormatValidatorApi dfv;

	@Test
	void testValidateDataFormat() {
		
		WebTestClient client = WebTestClient.bindToController(dfv).build();
		
		client
			.get()
			.uri("/api/atributos/data-validator?type=CANTIDAD&data=123")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.isEqualTo("true");
			});
			
		client
			.get()
			.uri("/api/atributos/data-validator?type=CANTIDAD&data=asd")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.isEqualTo("false");
		});
	}

}
