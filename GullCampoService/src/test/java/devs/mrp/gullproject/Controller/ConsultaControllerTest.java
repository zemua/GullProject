package devs.mrp.gullproject.Controller;

import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaAbstracta;
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
	
	PropuestaAbstracta prop1;
	PropuestaAbstracta prop2;
	Consulta consulta1;
	Consulta consulta2;
	Mono<Consulta> mono1;
	Mono<Consulta> mono2;
	
	@BeforeEach
	void init() {
		prop1 = new PropuestaAbstracta() {};
		prop1.addLineaId("linea1");
		prop1.addLineaId("linea2");
		prop1.setNombre("propuesta 1");
		
		prop2 = new PropuestaAbstracta() {};
		prop2.addLineaId("linea3");
		prop2.setNombre("propuesta 2");
		
		consulta1 = new Consulta();
		consulta1.setNombre("consulta 1");
		consulta1.setStatus("estado 1");
		consulta1.setId("idConsulta1");
		consulta1.addPropuesta(prop1);
		consulta1.addPropuesta(prop2);
		
		
		consulta2 = new Consulta();
		consulta2.setNombre("consulta 2");
		consulta2.setStatus("estado 2");
		consulta2.setId("idConsulta2");
		
		mono1 = Mono.just(consulta1);
		mono2 = Mono.just(consulta2);
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
						.contains("Crear nueva consulta");
					/**
					 * Now the info is passed through rest to the javascript datasheet
					 * so we don't include below data in the template anymore
					 */
						/*.contains("consulta 1")
						.contains("estado 1")
						.contains("revisar")
						.contains("consulta 2")
						.contains("estado 2");*/
			});
	}
	
	@Test
	void testReviewConsultaById() throws Exception {
		
		PropuestaAbstracta prop1 = new PropuestaAbstracta() {};
		prop1.addLineaId("linea1");
		prop1.addLineaId("linea2");
		prop1.setNombre("propuesta 1");
		PropuestaAbstracta prop2 = new PropuestaAbstracta() {};
		prop2.addLineaId("linea3");
		prop2.setNombre("propuesta 2");
		
		Consulta a = new Consulta();
		a.setNombre("consulta 1");
		a.setStatus("estado 1");
		a.setId("idConsulta1");
		a.addPropuesta(prop1);
		a.addPropuesta(prop2);
		
		
		Consulta b = new Consulta();
		b.setNombre("consulta 2");
		b.setStatus("estado 2");
		b.setId("idConsulta2");
		
		Mono<Consulta> mono1 = Mono.just(a);
		Mono<Consulta> mono2 = Mono.just(b);
		when(consultaService.findById(ArgumentMatchers.eq(a.getId()))).thenReturn(mono1);
		when(consultaService.findById(ArgumentMatchers.eq(b.getId()))).thenReturn(mono2);
		
		webTestClient.get()
			.uri("/consultas/revisar/id/idConsulta1")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nombre")
						.contains("Estado")
						.contains("Creado")
						.contains("Editado")
						.contains("Nombre Propuesta")
						.contains("Cantidad de Lineas")
						.contains("consulta 1")
						.contains("estado 1")
						.contains("propuesta 1")
						.contains("propuesta 2")
						.doesNotContain("Añadir una solicitud de propuesta del cliente");
			});
		
		webTestClient.get()
		.uri("/consultas/revisar/id/idConsulta2")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre")
					.contains("Estado")
					.contains("Creado")
					.contains("Editado")
					.contains("Nombre Propuesta")
					.contains("Cantidad de Lineas")
					.contains("consulta 2")
					.contains("estado 2")
					.doesNotContain("propuesta 1")
					.doesNotContain("propuesta 2")
					.contains("Añadir una solicitud de propuesta del cliente");
		});
	}
	
	@Test
	void testAddPropuestaToId() throws Exception {
		
		webTestClient.get()
			.uri("/consultas/revisar/id/idConsulta1/addpropuesta")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nueva Solicitud de Propuesta")
						.contains("Nombre")
						.contains("Ok")
						.contains("Volver");
			});
	}
	
	@Test
	void testProcessNewPropuesta() {
		
		consulta2.addPropuesta(prop1);
		when(consultaService.addPropuesta(ArgumentMatchers.eq(consulta2.getId()), ArgumentMatchers.any(PropuestaAbstracta.class))).thenReturn(Mono.just(consulta2));
		
		webTestClient.post()
		.uri("/consultas/revisar/id/idConsulta2")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("nombre", prop1.getNombre())
				.with("parentId", consulta2.getId()))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Gull Project - Propuesta Guardada")
					.contains("Propuesta Guardada Como...")
					.contains("Nombre:")
					.doesNotContain("errores")
					.contains(prop1.getNombre())
					.contains("Volver a la consulta");
		});
		
		prop1.setNombre("");
		consulta2.addPropuesta(prop1);
		
		webTestClient.post()
		.uri("/consultas/revisar/id/idConsulta2")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("nombre", "")
				.with("parentId", consulta2.getId()))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nueva Solicitud de Propuesta")
					.contains("Corrige los errores y reenvía.")
					.contains("Selecciona un nombre")
					.contains("Nombre:")
					.contains("Volver");
		});
	}
	
	@Test
	void testDeleteConsultaById() throws Exception {
		
		when(consultaService.findById(ArgumentMatchers.eq(consulta1.getId()))).thenReturn(mono1);
		
		webTestClient.get()
			.uri("/consultas/delete/id/idConsulta1")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Borrar Consulta")
						.contains("¿Seguro que deseas borrar esta consulta?")
						.contains("Nombre:")
						.contains("Estado:")
						.contains(consulta1.getNombre())
						.contains(consulta1.getStatus())
						.contains("Volver");
			});
	}
	
	@Test
	void testProcessDeleteConsultaById() {
		
		when(consultaService.deleteById(ArgumentMatchers.anyString())).thenReturn(Mono.just(0L));
		when(consultaService.deleteById(ArgumentMatchers.eq(consulta1.getId()))).thenReturn(Mono.just(1L)); // latest rules
		
		webTestClient.post()
		.uri("/consultas/delete/id/idConsulta1")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("id", consulta1.getId())
				.with("nombre", consulta1.getNombre())
				.with("status", consulta1.getStatus()))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Borrar Consulta")
					.contains("Consulta borrada correctamente")
					.doesNotContain("Algo no ha ido correctamente")
					.contains("Volver");
		});
		
		webTestClient.post()
		.uri("/consultas/delete/id/idConsulta2")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("id", consulta2.getId())
				.with("nombre", consulta2.getNombre())
				.with("status", consulta2.getStatus()))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Borrar Consulta")
					.doesNotContain("Consulta borrada correctamente")
					.contains("Algo no ha ido correctamente")
					.contains("Volver");
		});
		
	}
	
	@Test
	void testDeletePropuestaById() throws Exception {
		
		when(consultaService.findById(ArgumentMatchers.eq(consulta1.getId()))).thenReturn(mono1);
		
		webTestClient.get()
			.uri("/consultas/delete/id/"+consulta1.getId()+"/propuesta/"+prop1.getId())
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Borrar Propuesta")
						.contains("¿Seguro que deseas borrar esta propuesta?")
						.contains("Nombre:")
						.contains(prop1.getNombre())
						.contains("Volver");
			});
	}
	
	@Test
	void testProcessDeletePropuestaById() throws Exception {
		
		when(consultaService.findById(ArgumentMatchers.eq(consulta1.getId()))).thenReturn(mono1);
		Consulta consulta3 = consulta1;
		consulta3.removePropuesta(prop1);
		when(consultaService.removePropuesta(consulta1.getId(), prop1)).thenReturn(Mono.just(consulta3));
		when(consultaService.removePropuestaById(consulta1.getId(), prop1.getId())).thenReturn(Mono.just(consulta3));
		
		webTestClient.post()
			.uri("/consultas/delete/id/"+consulta1.getId()+"/propuesta/"+prop1.getId())
			.accept(MediaType.TEXT_HTML)
			.body(BodyInserters.fromFormData("idConsulta", consulta1.getId())
					.with("idPropuesta", prop1.getId()))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Borrar Propuesta")
						.contains("Borrado procesado.")
						.contains("Volver");
			});
	}

}
