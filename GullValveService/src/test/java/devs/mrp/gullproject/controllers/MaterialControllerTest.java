package devs.mrp.gullproject.controllers;

import static org.mockito.Mockito.when;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import devs.mrp.gullproject.domains.Material;
import devs.mrp.gullproject.repositorios.MaterialRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
//@DataMongoTest
//@WebFluxTest(MaterialController.class)
@SpringBootTest
@AutoConfigureWebTestClient
class MaterialControllerTest {
	
	@Autowired
	WebTestClient webTestClient;
	
	@MockBean
	MaterialRepo materialRepo;
	
	@Test
	public void testMostrarMateriales() {
		
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
	
	@Test
	public void testCrearMaterial() {
		
		Matcher<String> nuevoMaterialString = new ContainsMatcher("Nuevo Material");
		
		webTestClient.get()
			.uri("/material/nuevo")
			.accept(MediaType.ALL)
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.value(nuevoMaterialString);
	}
	
	@Test
	public void testProcesaMaterial() {
		
		Material m = new Material();
		m.setName("epdm");
		Flux<Material> mFlux = Flux.just(m);
		Mono<Material> mMono = Mono.just(m);
		when(materialRepo.findAll()).thenReturn(mFlux);
		when(materialRepo.save(m)).thenReturn(mMono);
		
		Matcher<String> epdmString = new ContainsMatcher("epdm");
		Matcher<String> materialesEnDb = new ContainsMatcher("Materiales en db");
		
		webTestClient.post()
		.uri("/material/nuevo")
		.accept(MediaType.ALL)
		.body(BodyInserters.fromValue(m))
		.exchange()
		.expectStatus().isCreated()
		.expectBody(String.class)
		.value(epdmString)
		.value(materialesEnDb);
		
	}
	
	class ContainsMatcher extends BaseMatcher<String> {
		
		String toComp;
		
		ContainsMatcher(String toComp){
			this.toComp = toComp;
		}
		
		public void setToComp(String s) {
			this.toComp = s;
		}

		@Override
		public boolean matches(Object actual) {
			if (actual instanceof String) {
			return ((String)actual).contains(toComp);
			}
			return false;
		}

		@Override
		public void describeTo(Description description) {
			
		}
	}

}
