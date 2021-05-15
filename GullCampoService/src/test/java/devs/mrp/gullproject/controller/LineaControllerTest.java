package devs.mrp.gullproject.controller;

import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import devs.mrp.gullproject.configuration.MapperConfig;
import devs.mrp.gullproject.domains.AtributoForCampo;
import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.Linea;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaCliente;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.LineaService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = LineaController.class)
@AutoConfigureWebTestClient
@Import({MapperConfig.class})
class LineaControllerTest {
	
	WebTestClient webTestClient;
	LineaController lineaController;
	
	@MockBean
	LineaService lineaService;
	@MockBean
	ConsultaService consultaService;
	@MockBean
	AtributoServiceProxyWebClient atributoService;
	
	@Autowired
	public LineaControllerTest(WebTestClient webTestClient, LineaController lineaController) {
		this.webTestClient = webTestClient;
		this.lineaController = lineaController;
	}
	
	Linea linea1;
	Campo<String> campo1a;
	Campo<Integer> campo1b;
	
	Linea linea2;
	Campo<String> campo2a;
	Campo<Integer> campo2b;
	
	Mono<Linea> mono1;
	Mono<Linea> mono2;
	Flux<Linea> flux;
	
	AtributoForCampo atributo1;
	AtributoForCampo atributo2;
	AtributoForCampo atributo3;
	AtributoForCampo atributo4;
	Flux<AtributoForCampo> fluxAttsPropuesta;
	
	Propuesta propuesta;
	Consulta consulta;
	
	@BeforeEach
	void init() {
		propuesta = new PropuestaCliente();
		propuesta.setNombre("propuestaName");
		consulta = new Consulta();
		consulta.addPropuesta(propuesta);
		
		campo1a = new Campo<>();
		campo1a.setAtributoId("atributo1");
		campo1a.setDatos("datos1");
		campo1b = new Campo<>();
		campo1b.setAtributoId("atributo2");
		campo1b.setDatos(123);
		linea1 = new Linea();
		linea1.addCampo(campo1a);
		linea1.addCampo(campo1b);
		linea1.setNombre("nombre linea 1");
		linea1.setPropuestaId(propuesta.getId());
		
		campo2a = new Campo<>();
		campo2a.setAtributoId("atributo1");
		campo2a.setDatos("datos2");
		campo2b = new Campo<>();
		campo2b.setAtributoId("atributo2");
		campo2b.setDatos(321);
		linea2 = new Linea();
		linea2.addCampo(campo2a);
		linea2.addCampo(campo2b);
		linea2.setPropuestaId(propuesta.getId());
		
		mono1 = Mono.just(linea1);
		mono2 = Mono.just(linea2);
		flux = Flux.just(linea1, linea2);
		
		atributo1 = new AtributoForCampo();
		atributo1.setName("atributo1");
		atributo2 = new AtributoForCampo();
		atributo2.setName("atributo2");
		atributo3 = new AtributoForCampo();
		atributo3.setName("atributo3");
		atributo4 = new AtributoForCampo();
		atributo4.setName("atributo4");
		fluxAttsPropuesta = Flux.just(atributo1, atributo2, atributo3, atributo4);
	}

	@Test
	void testShowAllLinesOf() {
		when(lineaService.findByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(flux);
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		when(consultaService.findConsultaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(consulta));
		
		webTestClient.get()
			.uri("/lineas/allof/propid/" + propuesta.getId())
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Lineas de la propuesta")
						.contains("Crear nueva linea")
						.contains("Nombre")
						.contains("Campos")
						.contains("Enlace")
						.contains(propuesta.getNombre());
			});
	}
	
	@Test
	void testAddLineaToPropuesta() {
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(fluxAttsPropuesta);
		
		webTestClient.get()
		.uri("/lineas/of/" + propuesta.getId() + "/new")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nueva Linea en Propuesta: " + propuesta.getNombre())
					.contains("Nombre")
					.contains("Ok")
					.contains("Volver")
					.contains("atributo1")
					.contains("atributo2")
					.contains("atributo3")
					.contains("atributo4")
					.contains(propuesta.getNombre());
		});
	}
	
	@Test
	void testProcessAddLineaToPropuesta() {
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		when(lineaService.addLinea(ArgumentMatchers.refEq(linea1, "id", "campos"))).thenReturn(Mono.just(linea1));
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.anyString())).thenReturn(Mono.just("String"));
		
		// all fine
		webTestClient.post()
		.uri("/lineas/of/" + propuesta.getId() + "/new")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("linea.nombre", linea1.getNombre())
				.with("linea.id", linea1.getId())
				.with("linea.propuestaId", propuesta.getId())
				.with("attributes[0].id", atributo1.getId())
				.with("attributes[0].value", "valor de att 1")
				.with("attributes[0].localIdentifier", "localIdentifier")
				.with("attributes[0].name", atributo1.getName())
				.with("attributes[0].tipo", "tipo")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Gull Project - Linea Guardada")
					.contains("Linea Guardada Como...")
					.contains("Nombre:")
					.doesNotContain("errores")
					.contains(propuesta.getNombre())
					.contains(linea1.getNombre())
					.contains("Volver a la propuesta");
		});
		
		// with different ids on the url and the object
		webTestClient.post()
		.uri("/lineas/of/incorrectid/new")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("nombre", linea1.getNombre())
				.with("id", linea1.getId())
				.with("propuestaId", propuesta.getId())
				.with("attributes[0].id", atributo1.getId())
				.with("attributes[0].value", "valor de att 1")
				.with("attributes[0].localIdentifier", "localIdentifier")
				.with("attributes[0].name", atributo1.getName())
				.with("attributes[0].tipo", "tipo")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Gull Project - Linea Guardada")
					.doesNotContain("Linea Guardada Como...")
					.doesNotContain("Nombre:")
					.doesNotContain("errores")
					.doesNotContain(propuesta.getNombre())
					.doesNotContain(linea1.getNombre())
					.doesNotContain("Volver a la propuesta")
					.contains("Algo no ha ido bien...");
		});
		
		// with validation error
		linea1.setNombre("");
		webTestClient.post()
		.uri("/lineas/of/" + propuesta.getId() + "/new")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("nombre", linea1.getNombre())
				.with("id", linea1.getId())
				.with("propuestaId", propuesta.getId())
				.with("attributes[0].id", atributo1.getId())
				.with("attributes[0].value", "valor de att 1")
				.with("attributes[0].localIdentifier", "localIdentifier")
				.with("attributes[0].name", atributo1.getName())
				.with("attributes[0].tipo", "tipo")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Gull Project - Nueva Linea en Propuesta")
					.doesNotContain("Linea Guardada Como...")
					.contains("Nombre:")
					.contains("Corrige los errores y reenvía")
					.contains("Selecciona un nombre")
					.contains("Ok")
					.doesNotContain("Volver a la propuesta");
		});
	}

}
