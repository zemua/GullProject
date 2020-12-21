package devs.mrp.gullproject.anotaciones;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.reactive.function.BodyInserters;

import devs.mrp.gullproject.controller.AtributoController;
import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import devs.mrp.gullproject.repositorios.AtributoRepo;
import devs.mrp.gullproject.service.AtributoService;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = AtributoController.class)
@AutoConfigureWebTestClient
class ValorEnumValidatorTest {
	
	@Autowired
	private WebTestClient webTestClient;
	
	@MockBean
	AtributoService atributoService;
	
	@Test
	public void givenAtributoUriPostWithPostAndFormatData_whenMockMvc_ThenVerifyErrorResponse() throws Exception {
		
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
			.body(BodyInserters.fromFormData("name", "nombre valido")
					.with("tipo", "esto es invalido"))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(response -> {
					Assertions.assertThat(response.getResponseBody()).asString()
						.contains("Nombre:")
						.contains("Tipo:")
						.contains("Nuevo Atributo")
						.contains("errores")
						.doesNotContain("El nombre es obligatorio")
						.contains("Escoge un tipo correcto")
						.doesNotContain("Guardado. ¿Añadir otro?");
			});
	}
	


}
