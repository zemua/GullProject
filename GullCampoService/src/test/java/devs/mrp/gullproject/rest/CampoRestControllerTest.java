package devs.mrp.gullproject.rest;

import static org.mockito.Mockito.when;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.models.CampoRepresentationModel;
import devs.mrp.gullproject.domains.models.CampoRepresentationModelAssembler;
import devs.mrp.gullproject.repository.CampoRepo;
import devs.mrp.gullproject.service.CampoService;
import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
//@SpringBootTest
@WebFluxTest(controllers = CampoRestController.class)
@Import({CampoService.class, HypermediaWebTestClientConfigurer.class})
class CampoRestControllerTest {

	@Autowired
	HypermediaWebTestClientConfigurer configurer;
	@Autowired
	CampoRestController campoRestController;
	
	@MockBean
	CampoRepo campoRepo;
	@MockBean
	CampoRepresentationModelAssembler crma;
	
	@Test
	void testGetAllCampos() {
		
		WebTestClient client = WebTestClient.bindToController(campoRestController).build().mutateWith(configurer);
		
		Campo<Integer> m = new Campo<>();
		m.setAtributoId("id_del_campo");
		m.setDatos(235);
		m.setId("id_aleatoria");
		Flux<Campo<?>> flux = Flux.just(m);
		
		Campo<Integer> m2 = new Campo<>();
		m2.setAtributoId("sin_id");
		m2.setDatos(000);
		m2.setId("sin_id");
		
		when(campoRepo.findAll()).thenReturn(flux);
		
		CampoRepresentationModel crm = new CampoRepresentationModel();
		crm.setAtributoId(m.getAtributoId());
		crm.setDatos(m.getDatos());
		crm.setId(m.getId());
		crm.add(Link.of("esto_es_un_link"));
		
		CampoRepresentationModel crm2 = new CampoRepresentationModel();
		crm2.setAtributoId(m2.getAtributoId());
		crm2.setDatos(m2.getDatos());
		crm2.setId(m2.getId());
		crm2.add(Link.of("no_link"));
		
		when(crma.toModel(ArgumentMatchers.any(Campo.class))).thenReturn(crm2);
		when(crma.toModel(ArgumentMatchers.eq(m))).thenReturn(crm);
		
		client.get()
			.uri("/api/campos/all").exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.value(new BaseMatcher<String>() {

				@Override
				public boolean matches(Object actual) {
					String a = actual.toString();
					return a.contains("id_del_campo")
							&& a.contains("235")
							&& a.contains("id_aleatoria");
				}

				@Override
				public void describeTo(Description description) {
				}
			});
	}

}
