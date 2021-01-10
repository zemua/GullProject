package devs.mrp.gullproject.Controller;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.service.ConsultaService;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ConsultaController.class)
@AutoConfigureWebTestClient
class ConsultaControllerTest {
	
	WebTestClient webTestClient;
	ConsultaController consultaController;
	
	@MockBean
	ConsultaService consultaService;
	
	@Autowired
	public ConsultaControllerTest(WebTestClient webTestClient, ConsultaController consultaController) {
		this.webTestClient = webTestClient;
		this.consultaController = consultaController;
	}
	

	@Test
	void testCrearAtributo() throws Exception {
		
		webTestClient.get()
			.uri("/consultas/nuevo")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nombre:")
						.contains("Estado:")
						.contains("Nueva Consulta")
						.doesNotContain("Guardada. ¿Añadir otra?");
			});
		
		webTestClient.get()
			.uri("/consultas/nuevo?add=1")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre:")
					.contains("Estado:")
					.contains("Nueva Consulta")
					.contains("Guardada. ¿Añadir otra?");
		});
	}

}
