package devs.mrp.gullproject.controllers;

import static org.mockito.Mockito.when;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.GullValveServiceApplication;
import devs.mrp.gullproject.domains.Material;
import devs.mrp.gullproject.repositorios.MaterialRepo;
import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
//@DataMongoTest
//@WebFluxTest
@SpringBootTest
@AutoConfigureWebTestClient
public class MaterialRestControllerTest {
	
	@Autowired
	WebTestClient webTestClient;
	@Autowired
	HypermediaWebTestClientConfigurer configurer;
	
	@MockBean
	MaterialRepo materialRepo;
	
	@Test
	public void testGetAllMaterials() {
		
		// Configure an application context programmatically.
		/*AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(GullValveServiceApplication.class); 
		context.refresh();*/
		
		// Extract the WebTestClientConfigurer from the app context.
		//HypermediaWebTestClientConfigurer configurer = context.getBean(HypermediaWebTestClientConfigurer.class);
		
		// Create a WebTestClient by binding to the controller and applying the hypermedia configurer.
		WebTestClient client = webTestClient.mutateWith(configurer);
		
		
		Material m = new Material();
		m.setName("epdm");
		m.setId("idaleatoria");
		Flux<Material> mFlux = Flux.just(m);
		
		when(materialRepo.findAll()).thenReturn(mFlux);
		
		client.get()
			.uri("/api/materiales")
			//.accept(MediaType.APPLICATION_JSON)
			.exchange() // TODO mirar por qué la petición hace timeout en este punto, revisar MaterialRepresentationModelAssembler.toModel
			.expectStatus().isOk()
			.expectBody(String.class)
			.value(new BaseMatcher<String>() {

				@Override
				public boolean matches(Object actual) {
					return actual.toString().contains("epdm");
				}

				@Override
				public void describeTo(Description description) {
					
				}
			});
		
	}
	
}
