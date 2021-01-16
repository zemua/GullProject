package devs.mrp.gullproject.Controller;

import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

import devs.mrp.gullproject.controller.ConsultaController;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.service.ConsultaService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
	void testCrearConsulta() throws Exception {
		
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
	
	@Test
	void testProcessNewConsulta() {
		Consulta a = new Consulta();
		a.setNombre("name of consulta");
		a.setStatus("open status");
		a.setId("idConsulta");
		
		Consulta b = new Consulta();
		b.setNombre("name of consulta");
		b.setStatus("open status");
		
		Mono<Consulta> mono1 = Mono.just(a);
		when(consultaService.save(ArgumentMatchers.refEq(b, "createdTime"))).thenReturn(mono1); // excludes "createdTime" from the match
		
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
	    formData.add("nombre", "name of consulta");
	    formData.add("status", "open status");
		
		webTestClient.post()
		.uri("/consultas/nuevo")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData(formData))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre:")
					.contains("Estado:")
					.contains("Consulta Guardada")
					.doesNotContain("errores")
					.contains("name of consulta")
					.contains("open status")
					.contains("Consulta Guardada Como...");
		});
		
		webTestClient.post()
		.uri("/consultas/nuevo")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("nombre", "")
				.with("status", "cancelled"))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre:")
					.contains("Estado:")
					.contains("Nueva Consulta")
					.contains("errores")
					.contains("El nombre es obligatorio")
					.doesNotContain("Guardada. ¿Añadir otra?")
					.doesNotContain("Consulta Guardada")
					.contains("cancelled");
		});
		
		webTestClient.post()
		.uri("/consultas/nuevo")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("nombre", "este es incorrecto")
				.with("status", ""))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre:")
					.contains("Estado:")
					.contains("Nueva Consulta")
					.contains("errores")
					.contains("El estado es obligatorio.")
					.doesNotContain("Guardada. ¿Añadir otra?")
					.doesNotContain("Consulta Guardada")
					.contains("este es incorrecto");
		});
	}
	
	@Test
	void testShowAllConsultas() throws Exception {
		
		Consulta a = new Consulta();
		a.setNombre("consulta 1");
		a.setStatus("estado 1");
		a.setId("idConsulta1");
		
		Consulta b = new Consulta();
		b.setNombre("consulta 2");
		b.setStatus("estado 2");
		b.setId("idConsulta2");
		
		Flux<Consulta> flux = Flux.just(a, b);
		when(consultaService.findAll()).thenReturn(flux);
		
		webTestClient.get()
			.uri("/consultas/all")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Todas Las Consultas")
						.contains("Crear nuevo atributo")
						.contains("consulta 1")
						.contains("estado 1")
						.contains("revisar")
						.contains("consulta 2")
						.contains("estado 2");
			});
	}

}
