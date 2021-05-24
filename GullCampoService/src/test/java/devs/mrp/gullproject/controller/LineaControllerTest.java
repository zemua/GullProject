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
import devs.mrp.gullproject.domains.dto.AtributoForFormDto;
import devs.mrp.gullproject.domains.dto.AtributoForLineaFormDto;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;
import devs.mrp.gullproject.service.ConsultaService;
import devs.mrp.gullproject.service.LineaService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
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
		atributo1 = new AtributoForCampo();
		atributo1.setId("id1");
		atributo1.setName("atributo1");
		atributo1.setTipo("DESCRIPCION");
		atributo2 = new AtributoForCampo();
		atributo2.setId("id2");
		atributo2.setName("atributo2");
		atributo2.setTipo("NUMERO");
		atributo3 = new AtributoForCampo();
		atributo3.setId("id3");
		atributo3.setName("atributo3");
		atributo3.setTipo("NUMERO");
		atributo4 = new AtributoForCampo();
		atributo4.setId("id4");
		atributo4.setName("atributo4");
		fluxAttsPropuesta = Flux.just(atributo1, atributo2, atributo3, atributo4);
		
		propuesta = new PropuestaCliente();
		propuesta.setNombre("propuestaName");
		consulta = new Consulta();
		consulta.addPropuesta(propuesta);
		
		campo1a = new Campo<>();
		campo1a.setAtributoId(atributo1.getId());
		campo1a.setDatos("datos1");
		campo1b = new Campo<>();
		campo1b.setAtributoId(atributo2.getId());
		campo1b.setDatos(123);
		linea1 = new Linea();
		linea1.addCampo(campo1a);
		linea1.addCampo(campo1b);
		linea1.setNombre("nombre linea 1");
		linea1.setPropuestaId(propuesta.getId());
		
		campo2a = new Campo<>();
		campo2a.setAtributoId(atributo2.getId());
		campo2a.setDatos("datos2");
		campo2b = new Campo<>();
		campo2b.setAtributoId(atributo3.getId());
		campo2b.setDatos(321);
		linea2 = new Linea();
		linea2.addCampo(campo2a);
		linea2.addCampo(campo2b);
		linea2.setPropuestaId(propuesta.getId());
		
		mono1 = Mono.just(linea1);
		mono2 = Mono.just(linea2);
		flux = Flux.just(linea1, linea2);
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
		when(lineaService.addLinea(ArgumentMatchers.any(Mono.class))).thenReturn(Mono.just(linea1));
		when(consultaService.findPropuestaByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Mono.just(propuesta));
		when(atributoService.getClassTypeOfFormat(ArgumentMatchers.anyString())).thenReturn(Mono.just("String"));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq("DESCRIPCION"), ArgumentMatchers.eq("valor de att 1"))).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(ArgumentMatchers.eq("CANTIDAD"), ArgumentMatchers.eq("valor de att 1"))).thenReturn(Mono.just(false));
		
		// all fine
		log.debug("add linea to propuesta primera ronda");
		webTestClient.post()
		.uri("/lineas/of/" + propuesta.getId() + "/new")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("linea.nombre", "nombre")
				.with("linea.id", "lineaid")
				.with("linea.propuestaId", propuesta.getId())
				.with("attributes[0].id", "idatt1")
				.with("attributes[0].value", "valor de att 1")
				.with("attributes[0].localIdentifier", "localIdentifier")
				.with("attributes[0].name", "nombre att 1")
				.with("attributes[0].tipo", "DESCRIPCION")
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Gull Project - Linea Guardada")
					.contains("Linea Guardada Como...")
					.contains("Nombre:")
					.contains("Campos...")
					.contains(String.valueOf(linea1.getCampoByIndex(0).getDatos()))
					.contains(String.valueOf(linea1.getCampoByIndex(1).getDatos()))
					.doesNotContain("errores")
					.contains(propuesta.getNombre())
					.contains(linea1.getNombre())
					.contains("Volver a la propuesta");
		});
		
		// with different ids on the url and the object
		log.debug("add linea to propuesta segunda ronda");
		webTestClient.post()
		.uri("/lineas/of/incorrectid/new")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("linea.nombre", linea1.getNombre())
				.with("linea.id", linea1.getId())
				.with("linea.propuestaId", propuesta.getId())
				.with("attributes[0].id", atributo1.getId())
				.with("attributes[0].value", "valor de att 1")
				.with("attributes[0].localIdentifier", "localIdentifier")
				.with("attributes[0].name", atributo1.getName())
				.with("attributes[0].tipo", "DESCRIPCION")
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
		log.debug("add linea to propuesta tercera ronda");
		linea1.setNombre("");
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
				.with("attributes[0].tipo", "DESCRIPCION")
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
		
		// with validation error
		log.debug("add linea to propuesta con error de validación de atributo");
		linea1.setNombre("valid name");
		webTestClient.post().uri("/lineas/of/" + propuesta.getId() + "/new")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML)
				.body(BodyInserters.fromFormData("linea.nombre", linea1.getNombre()).with("linea.id", linea1.getId())
						.with("linea.propuestaId", propuesta.getId()).with("attributes[0].id", atributo1.getId())
						.with("attributes[0].value", "valor de att 1")
						.with("attributes[0].localIdentifier", "localIdentifier")
						.with("attributes[0].name", atributo1.getName()).with("attributes[0].tipo", "CANTIDAD"))
				.exchange().expectStatus().isOk().expectBody().consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
							.contains("Gull Project - Nueva Linea en Propuesta")
							.doesNotContain("Linea Guardada Como...").contains("Nombre:")
							.contains("Corrige los errores y reenvía").contains("El valor no es correcto para este atributo").contains("Ok")
							.doesNotContain("Volver a la propuesta");
				});
	}
	
	@Test
	void testRevisarLinea() {
		when(lineaService.findById(ArgumentMatchers.eq(linea1.getId()))).thenReturn(Mono.just(linea1));
		propuesta.addAttribute(atributo2);
		propuesta.addAttribute(atributo3);
		when(consultaService.findAttributesByPropuestaId(ArgumentMatchers.eq(propuesta.getId()))).thenReturn(Flux.fromIterable(propuesta.getAttributeColumns()));
		
		// it shows atributo2 and atributo3 from propuesta, value of atributo2 from linea, and hidden value of atributo1 from linea
		webTestClient.get()
		.uri("/lineas/revisar/id/" + linea1.getId())
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
			.contains("Editar Linea")
			.doesNotContain("Corrige los errores y reenvía")
			.contains(linea1.getNombre())
			.doesNotContain(atributo1.getName())
			.contains(propuesta.getAttributeColumns().get(0).getName())
			.contains(propuesta.getAttributeColumns().get(1).getName())
			.contains(String.valueOf(linea1.getCampoByIndex(0).getDatos()))
			.contains(String.valueOf(linea1.getCampoByIndex(1).getDatos()));
		});
	}
	
	@Test
	void testProcessRevisarLinea() {
		propuesta.addAttribute(atributo2);
		propuesta.addAttribute(atributo3);
		
		AtributoForLineaFormDto att1 = new AtributoForLineaFormDto();
		att1.setId(atributo1.getId());
		att1.setName(atributo1.getName());
		att1.setTipo(atributo1.getTipo());
		att1.setValue(String.valueOf(linea1.getCampoByIndex(0).getDatos()));
		AtributoForLineaFormDto att2 = new AtributoForLineaFormDto();
		att2.setId(atributo2.getId());
		att2.setName(atributo2.getName());
		att2.setTipo(atributo2.getTipo());
		att2.setValue("85214");
		campo2a.setDatos(att2.getValue());
		AtributoForLineaFormDto att3 = new AtributoForLineaFormDto();
		att3.setId(atributo3.getId());
		att3.setName(atributo3.getName());
		att3.setTipo(atributo3.getTipo());
		att3.setValue("45789");
		campo2b.setDatos(Integer.parseInt(att3.getValue()));
		
		linea1.replaceOrElseAddCampo(atributo2.getId(), campo2a);
		linea1.replaceOrElseAddCampo(atributo3.getId(), campo2b);
		when(lineaService.updateLinea(ArgumentMatchers.any(Linea.class))).thenReturn(Mono.just(linea1));
		
		log.debug("response when all correct");
		when(atributoService.validateDataFormat(att1.getTipo(), att1.getValue())).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(att2.getTipo(), att2.getValue())).thenReturn(Mono.just(true));
		when(atributoService.validateDataFormat(att3.getTipo(), att3.getValue())).thenReturn(Mono.just(true));
		when(atributoService.getClassTypeOfFormat(att1.getTipo())).thenReturn(Mono.just("String"));
		when(atributoService.getClassTypeOfFormat(att2.getTipo())).thenReturn(Mono.just("Integer"));
		when(atributoService.getClassTypeOfFormat(att3.getTipo())).thenReturn(Mono.just("Integer"));
		
		webTestClient.post()
		.uri("/lineas/revisar/id/" + linea1.getId())
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("linea.nombre", linea1.getNombre())
				.with("linea.id", linea1.getId())
				.with("linea.propuestaId", linea1.getPropuestaId())
				
				.with("linea.campos[0].id", campo1a.getId())
				.with("linea.campos[0].atributoId", campo1a.getAtributoId())
				.with("linea.campos[0].datos", campo1a.getDatos())
				
				.with("linea.campos[1].id", campo1b.getId())
				.with("linea.campos[1].atributoId", campo1b.getAtributoId())
				.with("linea.campos[1].datos", String.valueOf(campo1b.getDatos()))
				
				.with("attributes[0].id", att2.getId())
				.with("attributes[0].value", att2.getValue())
				.with("attributes[0].localIdentifier", att2.getLocalIdentifier())
				.with("attributes[0].name", att2.getName())
				.with("attributes[0].tipo", att2.getTipo())
				
				.with("attributes[1].id", att3.getId())
				.with("attributes[1].value", att3.getValue())
				.with("attributes[1].localIdentifier", att3.getLocalIdentifier())
				.with("attributes[1].name", att3.getName())
				.with("attributes[1].tipo", att3.getTipo())
				)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Linea Actualizada")
					.contains("Linea Guardada Como...")
					.contains(linea1.getNombre())
					.contains(String.valueOf(linea1.getCampoByIndex(0).getDatos()))
					.contains(String.valueOf(linea1.getCampoByIndex(1).getDatos()))
					.contains(String.valueOf(linea1.getCampoByIndex(2).getDatos()))
					;
		});
	}

}
