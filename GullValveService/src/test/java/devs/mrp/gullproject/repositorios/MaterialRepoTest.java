package devs.mrp.gullproject.repositorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Material;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class MaterialRepoTest {

	@Test
	void testFindAllByName(@Autowired MaterialRepo materialRepo) {
		
		String name = "rnomae";
		Material m = new Material();
		m.setName(name);
		materialRepo.save(m).block();
		
		Flux<Material> flux = materialRepo.findAllByName(name);
		
		StepVerifier.create(flux)
			.assertNext(material -> {
				assertEquals(name, material.getName());
				assertNotNull(material.getId());
				
			})
			.expectComplete()
			.verify();
		
	}

}
