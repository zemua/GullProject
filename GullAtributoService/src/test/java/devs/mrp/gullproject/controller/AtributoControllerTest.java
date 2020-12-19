package devs.mrp.gullproject.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.assertj.core.api.Assertions;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.BodyInserters;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import devs.mrp.gullproject.GullAtributoServiceApplication;
import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import devs.mrp.gullproject.domains.representationmodels.AtributoRepresentationModelAssembler;
import devs.mrp.gullproject.repositorios.AtributoRepo;
import devs.mrp.gullproject.rest.AtributoRestController;
import devs.mrp.gullproject.rest.AtributoRestControllerById;
import devs.mrp.gullproject.service.AtributoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = AtributoController.class)
@AutoConfigureWebTestClient
class AtributoControllerTest {
	
	// TODO completar todos los tests

	@Autowired
	WebTestClient webTestClient;
	@Autowired
	AtributoController atributoController;
	
	@MockBean
	AtributoService atributoService;
	
	@MockBean
	AtributoRestController arc;
	@MockBean
	AtributoRestControllerById arcbi;
	
	@Test
	void testCrearAtributo() throws Exception {
		
		webTestClient.get()
			.uri("/atributos/nuevo")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nombre:")
						.contains("Tipo:")
						.contains("Nuevo Atributo")
						.doesNotContain("Guardado. ¿Añadir otro?");
			});
		
		webTestClient.get()
		.uri("/atributos/nuevo?add=1")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre:")
					.contains("Tipo:")
					.contains("Nuevo Atributo")
					.contains("Guardado. ¿Añadir otro?");
		});
	}
	
	@Test
	void testProcesarNuevoAtributo() throws Exception {
		Atributo a = new Atributo();
		a.setName("nombre");
		a.setTipo(DataFormat.CANTIDAD);
		a.setId("idDelMoNo");
		
		Atributo b = new Atributo();
		b.setName("nombre");
		b.setTipo(DataFormat.CANTIDAD);
		
		Mono<Atributo> mono = Mono.just(a);
		when(atributoService.save(ArgumentMatchers.eq(b))).thenReturn(mono);
		
		webTestClient.post()
		.uri("/atributos/nuevo")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("name", "nombre")
				.with("tipo", "CANTIDAD"))
		.exchange()
		.expectStatus().is3xxRedirection();
		
		webTestClient.post()
		.uri("/atributos/nuevo")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("name", "")
				.with("tipo", "asdvasdas"))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre:")
					.contains("Tipo:")
					.contains("Nuevo Atributo")
					.contains("errores")
					.contains("El nombre es obligatorio")
					.contains("Escoge un tipo correcto")
					.doesNotContain("Guardado. ¿Añadir otro?");
		});
	}
	
	@Test
	void testMostrarAtributos() {
		
		Atributo a = new Atributo();
		a.setName("nombreA");
		a.setTipo(DataFormat.CANTIDAD);
		a.setId("idDelMoNoA");
		
		Atributo b = new Atributo();
		b.setName("nombreB");
		b.setTipo(DataFormat.CANTIDAD);
		b.setId("idDelMoNoB");
		
		Flux<Atributo> flux = Flux.just(a, b);
		when(atributoService.findAll()).thenReturn(flux);
		
		webTestClient.get()
		.uri("/atributos/todos")
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("nombreA")
					.contains("nombreB")
					.contains("Todos Los Atributos")
					.doesNotContain(" - editar");
		});
	}
	
	@Test
	void testEditarAtributo() {
		
		Atributo a = new Atributo();
		a.setName("nombreA");
		a.setTipo(DataFormat.CANTIDAD);
		a.setId("idDelMoNoA");
		
		Mono<Atributo> mono = Mono.just(a);
		when(atributoService.findById(a.getId())).thenReturn(mono);
		
		webTestClient.get()
		.uri("/atributos/editar/id/"+a.getId())
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Editar Atributo")
					.contains("Nombre:")
					.contains("Tipo")
					.doesNotContain("error");
		});
	}
	
	@Test
	void actualizarAtributo() {
		
		Atributo a = new Atributo();
		a.setName("un nombre cualquiera");
		a.setTipo(DataFormat.CANTIDAD);
		a.setId("idDelMoNoA");
		
		Mono<Atributo> mono = Mono.just(a);
		when(atributoService.save(ArgumentMatchers.eq(a))).thenReturn(mono);
		
		webTestClient.post()
		.uri("/atributos/actualizar/id/"+a.getId())
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("name", "")
				.with("tipo", "asdvasdas"))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Nombre:")
					.contains("Tipo:")
					.contains("Editar Atributo")
					.contains("errores")
					.contains("El nombre es obligatorio")
					.contains("Selecciona un tipo de atributo aceptado")
					.doesNotContain("Guardado. ¿Añadir otro?");
		});
		
		webTestClient.post()
		.uri("/atributos/actualizar/id/"+a.getId())
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("name", "un nombre cualquiera")
				.with("tipo", "CANTIDAD"))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Atributo Actualizado")
					.contains("ID:")
					.contains("Nombre:")
					.contains("Tipo:")
					.contains("Volver")
					.contains("un nombre cualquiera")
					.contains("CANTIDAD");
		});
		
	}
	
	@Test
	void testBorrarAtributo() {
		
		Atributo a = new Atributo();
		a.setName("nombreA");
		a.setTipo(DataFormat.CANTIDAD);
		a.setId("idDelMoNoA");
		
		Mono<Atributo> mono = Mono.just(a);
		when(atributoService.findById(a.getId())).thenReturn(mono);
		
		webTestClient.get()
		.uri("/atributos/borrar/id/"+a.getId())
		.accept(MediaType.TEXT_HTML)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Borrar Atributo")
					.contains("ID:")
					.contains("Nombre:")
					.contains("Tipo")
					.contains("Confirmar borrado")
					.doesNotContain("error");
		});
	}
	
	@Test
	void testConfirmBorrarAtributo() {
		
		Atributo a = new Atributo();
		a.setName("un nombre cualquiera");
		a.setTipo(DataFormat.CANTIDAD);
		a.setId("idDelMoNoA");
		
		Mono<Atributo> mono = Mono.just(a);
		when(atributoService.deleteById(a.getId())).thenReturn(Mono.empty());
		
		webTestClient.post()
		.uri("/atributos/borrar/id/"+a.getId())
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("name", "un nombre cualquiera")
				.with("tipo", "CANTIDAD")
				.with("id", "idDelMoNoA"))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Borrar Atributo")
					.contains("El atributo ha sido borrado");
		});
		
		Atributo b = new Atributo();
		b.setName("");
		b.setTipo(DataFormat.CANTIDAD);
		b.setId("idDelMoNoA");
		
		Mono<Atributo> mona = Mono.just(b);
		when(atributoService.save(ArgumentMatchers.eq(b))).thenReturn(mona);
		
		webTestClient.post()
		.uri("/atributos/borrar/id/"+a.getId())
		.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		.accept(MediaType.TEXT_HTML)
		.body(BodyInserters.fromFormData("name", "")
				.with("tipo", "CANTIDAD"))
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> {
				Assertions.assertThat(response.getResponseBody()).asString()
					.contains("Error")
					.contains("no se ha podido realizar");
		});
		
	}

}
