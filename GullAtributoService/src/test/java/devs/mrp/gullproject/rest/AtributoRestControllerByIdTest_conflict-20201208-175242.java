package devs.mrp.gullproject.rest;

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
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = AtributoRepo.class)
//@AutoConfigureWebTestClient
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
		tipo.setDataFormat(DataFormat.ATRIBUTO_TEXTO);

		Atributo m = new Atributo();
		m.setName("seal");
		m.setId("idaleatoria");
		m.setTipo(DataFormat.ATRIBUTO_TEXTO);
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
			.value(new BaseMatcher<String>() {

				@Override
				public boolean matches(Object actual) {
					return actual.toString().contains("seal") &&
							actual.toString().contains("idaleatoria") &&
							actual.toString().contains("type name") &&
							actual.toString().contains("/api/esto/es/un/link");
				}

				@Override
				public void describeTo(Description description) {
				}
			});
		
	}

}
