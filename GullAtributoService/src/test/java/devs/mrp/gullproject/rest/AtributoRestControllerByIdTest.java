package devs.mrp.gullproject.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import devs.mrp.gullproject.domains.Tipo;
import devs.mrp.gullproject.domains.representationmodels.AtributoRepresentationModel;
import devs.mrp.gullproject.domains.representationmodels.AtributoRepresentationModelAssembler;
import devs.mrp.gullproject.repositorios.AtributoRepo;
import devs.mrp.gullproject.service.AtributoService;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = AtributoRestControllerById.class)
//@AutoConfigureWebTestClient
@Import({AtributoService.class})
class AtributoRestControllerByIdTest {
	
	//@Autowired
	//WebTestClient webTestClient;
	@Autowired
	HypermediaWebTestClientConfigurer configurer;
	@Autowired
	AtributoRestControllerById arc;
	
	@MockBean
	AtributoRepo atributoRepo;
	@MockBean
	AtributoRepresentationModelAssembler arma;

	@Test
	void testGetAtributoById() {
		
		//WebTestClient client = webTestClient.mutateWith(configurer);
		WebTestClient client = WebTestClient.bindToController(arc).build().mutateWith(configurer);
		
		Tipo tipo = new Tipo();
		tipo.setNombre("type name");
		tipo.setDataFormat(DataFormat.DESCRIPCION);

		Atributo m = new Atributo();
		m.setName("seal");
		m.setId("idaleatoria");
		m.setTipo(DataFormat.DESCRIPCION);
		m.setValoresFijos(true);
		Mono<Atributo> mono = Mono.just(m);
		
		when(atributoRepo.findById(ArgumentMatchers.anyString())).thenReturn(mono);
		AtributoRepresentationModel mrm = new AtributoRepresentationModel();
		mrm.setId(m.getId());
		mrm.setName(m.getName());
		mrm.setTipo(m.getTipo());
		mrm.setValoresFijos(m.isValoresFijos());
		mrm.add(Link.of("/api/esto/es/un/link"));
		when(arma.toModel(ArgumentMatchers.any(Atributo.class))).thenReturn(mrm);
		
		client.get()
			.uri("/api/atributos/id/idaleatoria")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.consumeWith(consumer -> {
				String s = consumer.getResponseBody();
				assertTrue(s.contains("seal"));
				assertTrue(s.contains("idaleatoria"));
				assertTrue(s.contains("DESCRIPCION"));
				assertTrue(s.contains("/api/esto/es/un/link"));
			});
		
	}

}
