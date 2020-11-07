package devs.mrp.gullproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Material;
import devs.mrp.gullproject.repositorios.MaterialRepo;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
@DataMongoTest
@ExtendWith(SpringExtension.class)
public class MongoDbSpringIntegrationTest {

	@Test
	public void test(@Autowired MaterialRepo materialRepo) {
		Material m = new Material();
		m.setName("testeandonombre");
		materialRepo.save(m).block();
		Flux<Material> materialFlux = materialRepo.findAllByName("testeandonombre");
		Flux<Material> todosFlux = materialRepo.findAll();
		
		log.debug(materialFlux.toString());
		
		StepVerifier
			.create(materialFlux)
			.assertNext(material -> {
				assertEquals("testeandonombre", material.getName());
				assertNotNull(material.getId());
			})
			.expectComplete()
			.verify();
		
		StepVerifier
			.create(todosFlux)
			.assertNext(material -> {
				assertEquals("testeandonombre", material.getName());
				assertNotNull(material.getId());
			})
			.expectComplete()
			.verify();
	}
	
}
