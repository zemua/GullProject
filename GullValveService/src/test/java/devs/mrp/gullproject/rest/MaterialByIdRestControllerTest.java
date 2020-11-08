package devs.mrp.gullproject.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import devs.mrp.gullproject.domains.Material;
import devs.mrp.gullproject.repositorios.MaterialRepo;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class MaterialByIdRestControllerTest {

	@Autowired
	WebTestClient webTestClient;
	
	@MockBean
	MaterialRepo materialRepo;
	
	@Test
	void testGetMaterialById() {
		
		Material m = new Material();
		m.setName("epdm");
		m.setId("idaleatoria");
		Mono<Material> mono = Mono.just(m);
		
		when(materialRepo.findById(ArgumentMatchers.anyString())).thenReturn(mono);
		
		webTestClient.get()
			.uri("/api/material"); // TODO completar test
		
		fail("Not yet implemented");
	}

}
