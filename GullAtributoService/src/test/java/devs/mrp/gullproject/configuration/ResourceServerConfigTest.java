package devs.mrp.gullproject.configuration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class ResourceServerConfigTest {
	
	@Autowired
	private WebTestClient client;

	@Test
	@WithMockUser
	void testValid() {
		client.get()
			.uri("/atributos/todos")
			.exchange()
			.expectStatus().isOk()
			;
	}
	
	@Test
	void testUnauthorized() {
		client.get()
			.uri("/consultas/all")
			.exchange()
			.expectStatus().is4xxClientError()
			;
	}

}
