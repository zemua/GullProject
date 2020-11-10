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

import devs.mrp.gullproject.domains.Material;
import devs.mrp.gullproject.domains.representationmodels.MaterialRepresentationModel;
import devs.mrp.gullproject.domains.representationmodels.representationmodelassemblersupport.MaterialRepresentationModelAssembler;
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
	MaterialRepresentationModelAssembler mrma;
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
		MaterialRepresentationModel mrm = new MaterialRepresentationModel();
		mrm.setId(m.getId());
		mrm.setName(m.getName());
		mrm.add(Link.of("/api/esto/es/un/link"));
		when(mrma.toModel(ArgumentMatchers.any(Material.class))).thenReturn(mrm);
		
		client.get()
			.uri("/api/materiales")
			//.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.value(new BaseMatcher<String>() {

				@Override
				public boolean matches(Object actual) {
					return actual.toString().contains("epdm") && 
							actual.toString().contains("idaleatoria") && 
							actual.toString().contains("esto/es/un/link");
				}

				@Override
				public void describeTo(Description description) {
					
				}
			});
		
	}
	
}
