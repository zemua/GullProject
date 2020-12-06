package devs.mrp.gullproject.rest;

import static org.mockito.Mockito.when;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.domains.Campo;
import devs.mrp.gullproject.domains.models.CampoRepresentationModel;
import devs.mrp.gullproject.domains.models.CampoRepresentationModelAssembler;
import devs.mrp.gullproject.repository.CampoRepo;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class CampoByIdRestControllerTest {

	@Autowired
	WebTestClient webTestClient;
	@Autowired
	HypermediaWebTestClientConfigurer configurer;
	
	@MockBean
	CampoRepo campoRepo;
	@MockBean
	CampoRepresentationModelAssembler crma;
	
	@Test
	void testGetCampoById() { // TODO completar
		
		WebTestClient client = webTestClient.mutateWith(configurer);

		Campo<Integer> m = new Campo<>();
		m.setAtributoId("id_del_campo");
		m.setDatos(235);
		m.setId("id_aleatoria");
		Mono<Campo<?>> mono = Mono.just(m);
		
		when(campoRepo.findById(ArgumentMatchers.anyString())).thenReturn(mono);
		CampoRepresentationModel crm = new CampoRepresentationModel();
		crm.setAtributoId(m.getAtributoId());
		crm.setDatos(m.getDatos());
		crm.setId(m.getId());
		crm.add(Link.of("/api/esto/es/un/link"));
		when(crma.toModel(ArgumentMatchers.any(Campo.class))).thenReturn(crm);
		
		webTestClient.get()
			.uri("/api/campos/id/id_aleatoria")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.value(new BaseMatcher<String>() {

				@Override
				public boolean matches(Object actual) {
					return actual.toString().contains("235") &&
							actual.toString().contains("id_del_campo") &&
							actual.toString().contains("id_aleatoria");
				}

				@Override
				public void describeTo(Description description) {
				}
				
			});
	}

}
