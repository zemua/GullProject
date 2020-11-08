package devs.mrp.gullproject.controllers;

import static org.mockito.Mockito.when;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.controllers.MaterialControllerTest.ContainsMatcher;
import devs.mrp.gullproject.domains.Material;
import devs.mrp.gullproject.repositorios.MaterialRepo;
import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@WebFluxTest
public class MaterialRestControllerTest {

	@Autowired
	WebTestClient webTestClient;
	
	@MockBean
	MaterialRepo materialRepo;
	
	@Test
	public void testGetAllMaterials() {
		Material m = new Material();
		m.setName("epdm");
		Flux<Material> mFlux = Flux.just(m);
		
		when(materialRepo.findAll()).thenReturn(mFlux);
		
		Matcher<String> epdmString = new ContainsMatcher("epdm");
		
		webTestClient.get()
			.uri("/material/todos")
			.accept(MediaType.ALL)
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.value(epdmString);
		
	}
	
}
