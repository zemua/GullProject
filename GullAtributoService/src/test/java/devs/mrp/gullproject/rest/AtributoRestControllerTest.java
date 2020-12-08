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
import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@AutoConfigureWebTestClient
@WebFluxTest(controllers = AtributoRestController.class)
@Import({AtributoService.class})
class AtributoRestControllerTest {
	
	@Autowired
	HypermediaWebTestClientConfigurer configurer;
	@Autowired
	AtributoRestController atributoRestController;
	
	@MockBean
	AtributoRepresentationModelAssembler arma;
	@MockBean
	AtributoRepo atributoRepo;

	@Test
	void testgetAllAtributos() {
		
		// Create a WebTestClient by binding to the controller and applying the hypermedia configurer.
		WebTestClient client = WebTestClient.bindToController(atributoRestController).build().mutateWith(configurer);
		
		Tipo tipo = new Tipo();
		tipo.setNombre("type name");
		tipo.setDataFormat(DataFormat.ATRIBUTO_TEXTO);
		
		Atributo m = new Atributo();
		m.setName("bonnet");
		m.setId("idaleatoria");
		m.setTipo(DataFormat.ATRIBUTO_TEXTO);
		m.setValoresFijos(true);
		Flux<Atributo> mFlux = Flux.just(m);
		
		when(atributoRepo.findAll()).thenReturn(mFlux);
		AtributoRepresentationModel mrm = new AtributoRepresentationModel();
		mrm.setId(m.getId());
		mrm.setName(m.getName());
		mrm.setTipo(m.getTipo());
		mrm.setValoresFijos(m.isValoresFijos());
		mrm.add(Link.of("/api/esto/es/un/link"));
		when(arma.toModel(ArgumentMatchers.eq(m))).thenReturn(mrm);
		
		client.get()
			.uri("/api/atributos/all")
			//.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.value(new BaseMatcher<String>() {

				@Override
				public boolean matches(Object actual) {
					return actual.toString().contains("bonnet") && 
							actual.toString().contains("idaleatoria") && 
							actual.toString().contains("ATRIBUTO_TEXTO") && 
							actual.toString().contains("esto/es/un/link");
				}

				@Override
				public void describeTo(Description description) {
					
				}
			});
		
	}

}
